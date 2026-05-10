package com.booknest.order.service;

import com.booknest.order.dto.PlaceOrderRequest;
import com.booknest.order.entity.Address;
import com.booknest.order.entity.Order;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface OrderService {

    // Place COD order
    Order placeOrder(PlaceOrderRequest request);

    // Place online/wallet order
    Order onlinePayment(PlaceOrderRequest request);

    // Get all orders (Admin)
    List<Order> getAllOrders();

    // Get orders by user
    List<Order> getOrdersByUserId(Long userId);

    // Get single order
    Optional<Order> getOrderById(Long orderId);

    // Get latest order of a user
    Optional<Order> getLatestOrderByUser(Long userId);

    // Admin: change order status
    Order changeStatus(Long orderId, String newStatus);

    // Delete order
    void deleteOrder(Long orderId);

    // Address management
    Address storeAddress(Address address);

    List<Address> getAddressByCustomer(Long customerId);

    List<Address> getAllAddresses();

    void deleteAddress(Long addressId);

    // Filter by status or date range
    List<Order> getOrdersByStatus(String status);

    List<Order> getOrdersByDateRange(LocalDate start, LocalDate end);
}
