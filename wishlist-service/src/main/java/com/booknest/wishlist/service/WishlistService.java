package com.booknest.wishlist.service;

import com.booknest.wishlist.entity.Wishlist;

import java.util.List;

public interface WishlistService {

    // Get or auto-create wishlist for user
    Wishlist getWishlistByUser(Long userId);

    // Add a book to wishlist
    Wishlist addBook(Long userId, Long bookId, String bookTitle, double bookPrice);

    // Remove a specific book from wishlist
    Wishlist removeBook(Long userId, Long bookId);

    // Clear entire wishlist
    void clearWishlist(Long userId);

    // Admin: get all wishlists
    List<Wishlist> getAllWishlists();
}
