package com.booknest.order.service;

import com.booknest.order.dto.PlaceOrderRequest;
import com.booknest.order.entity.Address;
import com.booknest.order.entity.Order;
import com.booknest.order.exception.OrderNotFoundException;
import com.booknest.order.repository.AddressRepository;
import com.booknest.order.repository.OrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class OrderServiceImpl implements OrderService {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private AddressRepository addressRepository;

    // ─── Order Placement ──────────────────────────────────────────────────────

    @Override
    @Transactional
    public Order placeOrder(PlaceOrderRequest request) {
        // Save address first if provided
        Address savedAddress = null;
        if (request.getAddress() != null) {
            request.getAddress().setCustomerId(request.getUserId());
            savedAddress = addressRepository.save(request.getAddress());
        }

        Order order = new Order();
        order.setUserId(request.getUserId());
        order.setBookId(request.getBookId());
        order.setBookTitle(request.getBookTitle());
        order.setQuantity(request.getQuantity());
        order.setAmountPaid(request.getAmountPaid());
        order.setModeOfPayment("COD");
        order.setOrderStatus("Placed");
        order.setAddress(savedAddress);

        return orderRepository.save(order);
    }

    @Override
    @Transactional
    public Order onlinePayment(PlaceOrderRequest request) {
        // Wallet balance validation is handled by wallet-service
        // Order service just records that payment mode was WALLET
        Address savedAddress = null;
        if (request.getAddress() != null) {
            request.getAddress().setCustomerId(request.getUserId());
            savedAddress = addressRepository.save(request.getAddress());
        }

        Order order = new Order();
        order.setUserId(request.getUserId());
        order.setBookId(request.getBookId());
        order.setBookTitle(request.getBookTitle());
        order.setQuantity(request.getQuantity());
        order.setAmountPaid(request.getAmountPaid());
        order.setModeOfPayment("WALLET");
        order.setOrderStatus("Confirmed");   // Wallet payment = instant confirmation
        order.setAddress(savedAddress);

        return orderRepository.save(order);
    }

    // ─── Read ─────────────────────────────────────────────────────────────────

    @Override
    public List<Order> getAllOrders() {
        return orderRepository.findAll();
    }

    @Override
    public List<Order> getOrdersByUserId(Long userId) {
        return orderRepository.findByUserId(userId);
    }

    @Override
    public Optional<Order> getOrderById(Long orderId) {
        return orderRepository.findById(orderId);
    }

    @Override
    public Optional<Order> getLatestOrderByUser(Long userId) {
        return orderRepository.findFirstByUserIdOrderByOrderIdDesc(userId);
    }

    @Override
    public List<Order> getOrdersByStatus(String status) {
        return orderRepository.findByOrderStatus(status);
    }

    @Override
    public List<Order> getOrdersByDateRange(LocalDate start, LocalDate end) {
        return orderRepository.findByOrderDateBetween(start, end);
    }

    // ─── Update ───────────────────────────────────────────────────────────────

    @Override
    @Transactional
    public Order changeStatus(Long orderId, String newStatus) {
        // Valid statuses from case study
        List<String> validStatuses = List.of(
                "Placed", "Confirmed", "Dispatched", "Delivered", "Cancelled");

        if (!validStatuses.contains(newStatus)) {
            throw new RuntimeException("Invalid status: " + newStatus +
                    ". Must be one of: " + validStatuses);
        }

        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new OrderNotFoundException("Order not found with id: " + orderId));

        order.setOrderStatus(newStatus);
        return orderRepository.save(order);
    }

    // ─── Delete ───────────────────────────────────────────────────────────────

    @Override
    public void deleteOrder(Long orderId) {
        if (!orderRepository.existsById(orderId)) {
            throw new OrderNotFoundException("Order not found with id: " + orderId);
        }
        orderRepository.deleteById(orderId);
    }

    // ─── Address Management ───────────────────────────────────────────────────

    @Override
    public Address storeAddress(Address address) {
        return addressRepository.save(address);
    }

    @Override
    public List<Address> getAddressByCustomer(Long customerId) {
        return addressRepository.findByCustomerId(customerId);
    }

    @Override
    public List<Address> getAllAddresses() {
        return addressRepository.findAll();
    }

    @Override
    public void deleteAddress(Long addressId) {
        addressRepository.deleteById(addressId);
    }
}
