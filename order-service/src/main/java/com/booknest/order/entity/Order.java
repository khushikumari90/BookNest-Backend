package com.booknest.order.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDate;

@Entity
@Table(name = "orders")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long orderId;

    @Column(nullable = false)
    private Long userId;

    // Book details stored directly (lightweight ref — no FK to book-service)
    @Column(nullable = false)
    private Long bookId;

    private String bookTitle;

    @Column(nullable = false)
    private int quantity;

    @Column(nullable = false)
    private double amountPaid;

    // "COD" or "WALLET"
    @Column(nullable = false)
    private String modeOfPayment;

    // Placed → Confirmed → Dispatched → Delivered | Cancelled
    @Column(nullable = false)
    private String orderStatus;

    @CreationTimestamp
    private LocalDate orderDate;

    // FK to Address table
    @ManyToOne(cascade = CascadeType.PERSIST)
    @JoinColumn(name = "address_id")
    private Address address;
}
