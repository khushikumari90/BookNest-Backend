package com.booknest.order.repository;

import com.booknest.order.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface OrderRepository extends JpaRepository<Order, Long> {

    List<Order> findByUserId(Long userId);

    List<Order> findByOrderStatus(String orderStatus);

    List<Order> findByOrderDateBetween(LocalDate start, LocalDate end);

    // Latest order for a user — used after checkout to return the created order
    Optional<Order> findFirstByUserIdOrderByOrderIdDesc(Long userId);

    List<Order> findByBookId(Long bookId);
}
