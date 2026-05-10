package com.booknest.book.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Table(name = "books")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Book {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long bookId;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private String author;

    @Column(unique = true)
    private String isbn;

    private String genre;
    private String publisher;

    @Column(nullable = false)
    private double price;

    @Column(nullable = false)
    private int stock;

    // Avg rating computed from review-service; stored here for quick reads
    private double rating;

    @Column(columnDefinition = "TEXT")
    private String description;

    private String coverImageUrl;

    private LocalDate publishedDate;

    // true = show on featured / new arrivals section
    private boolean featured;
}
