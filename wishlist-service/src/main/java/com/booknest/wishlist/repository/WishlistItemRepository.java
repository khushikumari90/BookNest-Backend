package com.booknest.wishlist.repository;

import com.booknest.wishlist.entity.WishlistItem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface WishlistItemRepository extends JpaRepository<WishlistItem, Long> {

    List<WishlistItem> findByWishlist_WishlistId(Long wishlistId);

    Optional<WishlistItem> findByWishlist_WishlistIdAndBookId(Long wishlistId, Long bookId);

    void deleteByWishlist_WishlistId(Long wishlistId);
}
