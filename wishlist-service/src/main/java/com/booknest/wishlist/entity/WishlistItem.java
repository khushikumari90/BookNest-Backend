package com.booknest.wishlist.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "wishlist_items")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class WishlistItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long itemId;

    @Column(nullable = false)
    private Long bookId;

    private String bookTitle;

    private double bookPrice;

    @ManyToOne
    @JoinColumn(name = "wishlist_id")
    @JsonBackReference
    private Wishlist wishlist;
}
