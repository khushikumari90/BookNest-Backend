package com.booknest.wishlist.service;

import com.booknest.wishlist.entity.Wishlist;
import com.booknest.wishlist.entity.WishlistItem;
import com.booknest.wishlist.exception.WishlistNotFoundException;
import com.booknest.wishlist.repository.WishlistItemRepository;
import com.booknest.wishlist.repository.WishlistRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class WishlistServiceImpl implements WishlistService {

    @Autowired
    private WishlistRepository wishlistRepository;

    @Autowired
    private WishlistItemRepository wishlistItemRepository;

    @Override
    public Wishlist getWishlistByUser(Long userId) {
        return wishlistRepository.findByUserId(userId)
                .orElseGet(() -> {
                    // Auto-create empty wishlist on first access
                    Wishlist w = new Wishlist();
                    w.setUserId(userId);
                    return wishlistRepository.save(w);
                });
    }

    @Override
    @Transactional
    public Wishlist addBook(Long userId, Long bookId, String bookTitle, double bookPrice) {
        Wishlist wishlist = getWishlistByUser(userId);

        // Prevent duplicate entries in wishlist
        boolean alreadyExists = wishlistItemRepository
                .findByWishlist_WishlistIdAndBookId(wishlist.getWishlistId(), bookId)
                .isPresent();

        if (alreadyExists) {
            throw new RuntimeException("Book is already in your wishlist.");
        }

        WishlistItem item = new WishlistItem();
        item.setBookId(bookId);
        item.setBookTitle(bookTitle);
        item.setBookPrice(bookPrice);
        item.setWishlist(wishlist);
        wishlist.getItems().add(item);

        return wishlistRepository.save(wishlist);
    }

    @Override
    @Transactional
    public Wishlist removeBook(Long userId, Long bookId) {
        Wishlist wishlist = wishlistRepository.findByUserId(userId)
                .orElseThrow(() -> new WishlistNotFoundException(
                        "Wishlist not found for userId: " + userId));

        WishlistItem item = wishlistItemRepository
                .findByWishlist_WishlistIdAndBookId(wishlist.getWishlistId(), bookId)
                .orElseThrow(() -> new RuntimeException(
                        "Book with id " + bookId + " not found in wishlist."));

        wishlist.getItems().remove(item);
        wishlistItemRepository.delete(item);
        return wishlistRepository.save(wishlist);
    }

    @Override
    @Transactional
    public void clearWishlist(Long userId) {
        Wishlist wishlist = wishlistRepository.findByUserId(userId)
                .orElseThrow(() -> new WishlistNotFoundException(
                        "Wishlist not found for userId: " + userId));
        wishlist.getItems().clear();
        wishlistRepository.save(wishlist);
    }

    @Override
    public List<Wishlist> getAllWishlists() {
        return wishlistRepository.findAll();
    }
}
