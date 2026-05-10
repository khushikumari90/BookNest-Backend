package com.booknest.wallet.service;

import com.booknest.wallet.dto.AddMoneyRequest;
import com.booknest.wallet.dto.PayMoneyRequest;
import com.booknest.wallet.entity.Statement;
import com.booknest.wallet.entity.Wallet;
import com.booknest.wallet.exception.InsufficientBalanceException;
import com.booknest.wallet.exception.WalletNotFoundException;
import com.booknest.wallet.repository.StatementRepository;
import com.booknest.wallet.repository.WalletRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class WalletServiceImpl implements WalletService {

    @Autowired
    private WalletRepository walletRepository;

    @Autowired
    private StatementRepository statementRepository;

    // ─── Helpers ─────────────────────────────────────────────────────────────

    private Statement buildStatement(Wallet wallet, String type,
                                     double amount, Long orderId, String remarks) {
        Statement stmt = new Statement();
        stmt.setWallet(wallet);
        stmt.setTransactionType(type);
        stmt.setAmount(amount);
        stmt.setOrderId(orderId);
        stmt.setTransactionRemarks(remarks);
        return stmt;
    }

    // ─── Wallet Lifecycle ─────────────────────────────────────────────────────

    @Override
    public Wallet createWallet(Long userId) {
        if (walletRepository.existsByUserId(userId)) {
            throw new RuntimeException("Wallet already exists for userId: " + userId);
        }
        Wallet wallet = new Wallet();
        wallet.setUserId(userId);
        wallet.setCurrentBalance(0.0);
        return walletRepository.save(wallet);
    }

    @Override
    public Wallet getWalletByUser(Long userId) {
        return walletRepository.findByUserId(userId)
                .orElseThrow(() -> new WalletNotFoundException(
                        "Wallet not found for userId: " + userId));
    }

    @Override
    public Wallet getWalletById(Long walletId) {
        return walletRepository.findById(walletId)
                .orElseThrow(() -> new WalletNotFoundException(
                        "Wallet not found with id: " + walletId));
    }

    @Override
    public List<Wallet> getAllWallets() {
        return walletRepository.findAll();
    }

    @Override
    public void deleteWallet(Long walletId) {
        if (!walletRepository.existsById(walletId)) {
            throw new WalletNotFoundException("Wallet not found with id: " + walletId);
        }
        walletRepository.deleteById(walletId);
    }

    // ─── Transactions ─────────────────────────────────────────────────────────

    @Override
    @Transactional
    public Wallet addMoney(Long userId, AddMoneyRequest request) {
        if (request.getAmount() <= 0) {
            throw new RuntimeException("Deposit amount must be greater than zero.");
        }

        Wallet wallet = getWalletByUser(userId);
        wallet.setCurrentBalance(wallet.getCurrentBalance() + request.getAmount());

        String remarks = request.getRemarks() != null
                ? request.getRemarks()
                : "Wallet top-up of ₹" + request.getAmount();

        Statement stmt = buildStatement(wallet, "DEPOSIT",
                request.getAmount(), null, remarks);
        wallet.getStatements().add(stmt);

        return walletRepository.save(wallet);
    }

    @Override
    @Transactional
    public Wallet payMoney(Long userId, PayMoneyRequest request) {
        if (request.getAmount() <= 0) {
            throw new RuntimeException("Payment amount must be greater than zero.");
        }

        Wallet wallet = getWalletByUser(userId);

        // NFR: wallet balance must be validated before processing — no double spend
        if (wallet.getCurrentBalance() < request.getAmount()) {
            throw new InsufficientBalanceException(
                    "Insufficient balance. Available: ₹" + wallet.getCurrentBalance()
                    + ", Required: ₹" + request.getAmount());
        }

        wallet.setCurrentBalance(wallet.getCurrentBalance() - request.getAmount());

        String remarks = request.getRemarks() != null
                ? request.getRemarks()
                : "Payment of ₹" + request.getAmount()
                  + (request.getOrderId() != null
                     ? " for Order #" + request.getOrderId() : "");

        Statement stmt = buildStatement(wallet, "WITHDRAW",
                request.getAmount(), request.getOrderId(), remarks);
        wallet.getStatements().add(stmt);

        return walletRepository.save(wallet);
    }

    // ─── Statement History ────────────────────────────────────────────────────

    @Override
    public List<Statement> getStatementsByUser(Long userId) {
        return statementRepository.findByWallet_UserId(userId);
    }

    @Override
    public List<Statement> getStatementsByType(String transactionType) {
        return statementRepository.findByTransactionType(transactionType.toUpperCase());
    }
}
