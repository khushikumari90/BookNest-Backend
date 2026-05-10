package com.booknest.wishlist.exception;

public class WishlistNotFoundException extends RuntimeException {
    public WishlistNotFoundException(String message) {
        super(message);
    }
}
