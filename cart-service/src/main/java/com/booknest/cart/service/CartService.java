package com.booknest.cart.service;

import com.booknest.cart.entity.Cart;

import java.util.List;

public interface CartService {

    // Get or create cart for user
    Cart getCartByUser(Long userId);

    // Add a book item to cart (creates cart if not exists)
    Cart addItem(Long userId, Long bookId, String bookTitle, double price, int quantity);

    // Remove a specific item from cart
    Cart removeItem(Long userId, Long itemId);

    // Update quantity of an existing cart item
    Cart updateQuantity(Long userId, Long itemId, int newQuantity);

    // Remove all items from cart
    void clearCart(Long userId);

    // Compute and return cart total
    double cartTotal(Long userId);

    // Admin: get all carts
    List<Cart> getAllCarts();
}
