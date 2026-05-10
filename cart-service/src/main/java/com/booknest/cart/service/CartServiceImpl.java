package com.booknest.cart.service;

import com.booknest.cart.entity.Cart;
import com.booknest.cart.entity.CartItem;
import com.booknest.cart.exception.CartNotFoundException;
import com.booknest.cart.exception.CartItemNotFoundException;
import com.booknest.cart.repository.CartItemRepository;
import com.booknest.cart.repository.CartRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class CartServiceImpl implements CartService {

    @Autowired
    private CartRepository cartRepository;

    @Autowired
    private CartItemRepository cartItemRepository;

    // ─── Helper ───────────────────────────────────────────────────────────────

    private void recalculateTotal(Cart cart) {
        double total = cart.getItems().stream()
                .mapToDouble(item -> item.getPrice() * item.getQuantity())
                .sum();
        cart.setTotalPrice(total);
    }

    // ─── Service Methods ─────────────────────────────────────────────────────

    @Override
    public Cart getCartByUser(Long userId) {
        return cartRepository.findByUserId(userId)
                .orElseGet(() -> {
                    // Auto-create empty cart on first access
                    Cart newCart = new Cart();
                    newCart.setUserId(userId);
                    newCart.setTotalPrice(0.0);
                    return cartRepository.save(newCart);
                });
    }

    @Override
    @Transactional
    public Cart addItem(Long userId, Long bookId, String bookTitle, double price, int quantity) {
        Cart cart = getCartByUser(userId);

        // Check if same book already in cart → increase quantity
        cartItemRepository.findByCart_CartIdAndBookId(cart.getCartId(), bookId)
                .ifPresentOrElse(
                        existingItem -> {
                            existingItem.setQuantity(existingItem.getQuantity() + quantity);
                            cartItemRepository.save(existingItem);
                        },
                        () -> {
                            CartItem newItem = new CartItem();
                            newItem.setBookId(bookId);
                            newItem.setBookTitle(bookTitle);
                            newItem.setPrice(price);
                            newItem.setQuantity(quantity);
                            newItem.setCart(cart);
                            cart.getItems().add(newItem);
                        }
                );

        recalculateTotal(cart);
        return cartRepository.save(cart);
    }

    @Override
    @Transactional
    public Cart removeItem(Long userId, Long itemId) {
        Cart cart = cartRepository.findByUserId(userId)
                .orElseThrow(() -> new CartNotFoundException("Cart not found for userId: " + userId));

        CartItem item = cartItemRepository.findById(itemId)
                .orElseThrow(() -> new CartItemNotFoundException("Cart item not found with id: " + itemId));

        cart.getItems().remove(item);
        cartItemRepository.delete(item);

        recalculateTotal(cart);
        return cartRepository.save(cart);
    }

    @Override
    @Transactional
    public Cart updateQuantity(Long userId, Long itemId, int newQuantity) {
        if (newQuantity <= 0) {
            // If quantity set to 0 or less, remove the item
            return removeItem(userId, itemId);
        }

        Cart cart = cartRepository.findByUserId(userId)
                .orElseThrow(() -> new CartNotFoundException("Cart not found for userId: " + userId));

        CartItem item = cartItemRepository.findById(itemId)
                .orElseThrow(() -> new CartItemNotFoundException("Cart item not found with id: " + itemId));

        item.setQuantity(newQuantity);
        cartItemRepository.save(item);

        recalculateTotal(cart);
        return cartRepository.save(cart);
    }

    @Override
    @Transactional
    public void clearCart(Long userId) {
        Cart cart = cartRepository.findByUserId(userId)
                .orElseThrow(() -> new CartNotFoundException("Cart not found for userId: " + userId));

        cart.getItems().clear();
        cart.setTotalPrice(0.0);
        cartRepository.save(cart);
    }

    @Override
    public double cartTotal(Long userId) {
        Cart cart = cartRepository.findByUserId(userId)
                .orElseThrow(() -> new CartNotFoundException("Cart not found for userId: " + userId));
        return cart.getTotalPrice();
    }

    @Override
    public List<Cart> getAllCarts() {
        return cartRepository.findAll();
    }
}
