package com.booknest.wallet.service;

import com.booknest.wallet.dto.AddMoneyRequest;
import com.booknest.wallet.dto.PayMoneyRequest;
import com.booknest.wallet.entity.Statement;
import com.booknest.wallet.entity.Wallet;

import java.util.List;

public interface WalletService {

    // Create wallet for a user (called at registration)
    Wallet createWallet(Long userId);

    // Get wallet by userId
    Wallet getWalletByUser(Long userId);

    // Get wallet by walletId
    Wallet getWalletById(Long walletId);

    // Top-up: add money to wallet
    Wallet addMoney(Long userId, AddMoneyRequest request);

    // Deduct money for purchase (returns updated wallet)
    Wallet payMoney(Long userId, PayMoneyRequest request);

    // Get all statements for a user (full transaction history)
    List<Statement> getStatementsByUser(Long userId);

    // Get statements by transaction type: DEPOSIT or WITHDRAW
    List<Statement> getStatementsByType(String transactionType);

    // Admin: get all wallets
    List<Wallet> getAllWallets();

    // Delete wallet
    void deleteWallet(Long walletId);
}
