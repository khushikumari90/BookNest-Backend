package com.booknest.wishlist.entity;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "wishlists")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Wishlist {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long wishlistId;

    @Column(nullable = false, unique = true)
    private Long userId;

    @CreationTimestamp
    private LocalDate createdAt;

    @OneToMany(mappedBy = "wishlist", cascade = CascadeType.ALL,
               orphanRemoval = true, fetch = FetchType.EAGER)
    @JsonManagedReference
    private List<WishlistItem> items = new ArrayList<>();
}
