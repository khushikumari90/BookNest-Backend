package com.booknest.wallet.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "statements")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Statement {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long statementId;

    // "DEPOSIT" or "WITHDRAW"
    @Column(nullable = false)
    private String transactionType;

    @Column(nullable = false)
    private double amount;

    @CreationTimestamp
    private LocalDateTime dateTime;

    // Linked to order if it's a purchase deduction; null for top-ups
    private Long orderId;

    private String transactionRemarks;

    @ManyToOne
    @JoinColumn(name = "wallet_id")
    @JsonBackReference
    private Wallet wallet;
}
