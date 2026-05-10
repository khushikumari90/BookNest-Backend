package com.booknest.wallet.entity;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "wallets")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Wallet {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long walletId;

    // One wallet per user — maps directly to userId from auth-service
    @Column(nullable = false, unique = true)
    private Long userId;

    @Column(nullable = false)
    private double currentBalance;

    // All transactions linked to this wallet
    @OneToMany(mappedBy = "wallet", cascade = CascadeType.ALL,
               orphanRemoval = true, fetch = FetchType.EAGER)
    @JsonManagedReference
    private List<Statement> statements = new ArrayList<>();
}
