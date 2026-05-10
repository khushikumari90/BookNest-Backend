package com.booknest.wallet.repository;

import com.booknest.wallet.entity.Statement;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface StatementRepository extends JpaRepository<Statement, Long> {

    List<Statement> findByWallet_WalletId(Long walletId);

    List<Statement> findByWallet_UserId(Long userId);

    List<Statement> findByTransactionType(String transactionType);

    Optional<Statement> findByOrderId(Long orderId);
}
