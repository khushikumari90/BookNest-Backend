package com.booknest.cart.repository;

import com.booknest.cart.entity.CartItem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CartItemRepository extends JpaRepository<CartItem, Long> {

    List<CartItem> findByCart_CartId(Long cartId);

    Optional<CartItem> findByCart_CartIdAndBookId(Long cartId, Long bookId);

    void deleteByCart_CartId(Long cartId);
}
