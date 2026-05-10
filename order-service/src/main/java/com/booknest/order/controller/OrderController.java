package com.booknest.order.controller;

import com.booknest.order.dto.PlaceOrderRequest;
import com.booknest.order.entity.Address;
import com.booknest.order.entity.Order;
import com.booknest.order.service.OrderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@CrossOrigin(origins = "*", allowedHeaders = "*")
@RestController
@RequestMapping("/orders")
@Tag(name = "Orders", description = "Order placement (COD & online), status management, and delivery address operations")
@SecurityRequirement(name = "BearerAuth")
public class OrderController {

    @Autowired
    private OrderService orderService;

    // ─── PLACE ORDER ──────────────────────────────────────────────────────────

    @Operation(summary = "Place a Cash on Delivery order",
               description = "Creates a new COD order. Payment is collected on delivery — no wallet deduction.")
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "COD order created", content = @Content(schema = @Schema(implementation = Order.class))),
        @ApiResponse(responseCode = "400", description = "Invalid request or out of stock", content = @Content)
    })
    @PostMapping("/place")
    public ResponseEntity<Order> placeOrder(@RequestBody PlaceOrderRequest request) {
        return new ResponseEntity<>(orderService.placeOrder(request), HttpStatus.CREATED);
    }

    @Operation(summary = "Place an online (Wallet/Razorpay) order",
               description = "Creates an order after online payment. Wallet deduction / Razorpay verification must be done before calling this endpoint.")
    @ApiResponse(responseCode = "201", description = "Online order created", content = @Content(schema = @Schema(implementation = Order.class)))
    @PostMapping("/online")
    public ResponseEntity<Order> onlinePayment(@RequestBody PlaceOrderRequest request) {
        return new ResponseEntity<>(orderService.onlinePayment(request), HttpStatus.CREATED);
    }

    // ─── GET ORDERS ───────────────────────────────────────────────────────────

    @Operation(summary = "Get all orders (Admin)", description = "Returns every order in the platform. Admin only.")
    @ApiResponse(responseCode = "200", description = "All orders list")
    @GetMapping("/all")
    public ResponseEntity<List<Order>> getAllOrders() {
        return ResponseEntity.ok(orderService.getAllOrders());
    }

    @Operation(summary = "Get orders by user", description = "Returns all orders placed by a specific customer.")
    @ApiResponse(responseCode = "200", description = "Orders for the user")
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<Order>> getOrdersByUser(
            @Parameter(description = "User ID", example = "1") @PathVariable Long userId) {
        return ResponseEntity.ok(orderService.getOrdersByUserId(userId));
    }

    @Operation(summary = "Get order by ID", description = "Returns a single order by its orderId.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Order found", content = @Content(schema = @Schema(implementation = Order.class))),
        @ApiResponse(responseCode = "404", description = "Order not found", content = @Content)
    })
    @GetMapping("/{orderId}")
    public ResponseEntity<Order> getOrderById(
            @Parameter(description = "Order ID", example = "10") @PathVariable Long orderId) {
        return orderService.getOrderById(orderId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @Operation(summary = "Get latest order by user", description = "Returns the most recent order for a user.")
    @ApiResponse(responseCode = "200", description = "Latest order returned")
    @GetMapping("/user/{userId}/latest")
    public ResponseEntity<Order> getLatestOrder(
            @Parameter(description = "User ID", example = "1") @PathVariable Long userId) {
        return orderService.getLatestOrderByUser(userId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @Operation(summary = "Filter orders by status",
               description = "Valid statuses: `PLACED`, `CONFIRMED`, `DISPATCHED`, `DELIVERED`, `CANCELLED`")
    @ApiResponse(responseCode = "200", description = "Orders with the given status")
    @GetMapping("/status/{status}")
    public ResponseEntity<List<Order>> getByStatus(
            @Parameter(description = "Order status", example = "PLACED") @PathVariable String status) {
        return ResponseEntity.ok(orderService.getOrdersByStatus(status));
    }

    @Operation(summary = "Filter orders by date range",
               description = "Returns orders placed between start and end dates (inclusive). Format: `YYYY-MM-DD`")
    @ApiResponse(responseCode = "200", description = "Orders in date range")
    @GetMapping("/date-range")
    public ResponseEntity<List<Order>> getByDateRange(
            @Parameter(description = "Start date (YYYY-MM-DD)", example = "2026-01-01") @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate start,
            @Parameter(description = "End date (YYYY-MM-DD)", example = "2026-12-31") @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate end) {
        return ResponseEntity.ok(orderService.getOrdersByDateRange(start, end));
    }

    // ─── UPDATE STATUS ────────────────────────────────────────────────────────

    @Operation(summary = "Update order status (Admin)",
               description = "Changes the order status. Valid values: `PLACED | CONFIRMED | DISPATCHED | DELIVERED | CANCELLED`\n\nBody: `{ \"status\": \"DISPATCHED\" }`")
    @ApiResponse(responseCode = "200", description = "Status updated, order returned")
    @PutMapping("/{orderId}/status")
    public ResponseEntity<Order> changeStatus(
            @Parameter(description = "Order ID", example = "10") @PathVariable Long orderId,
            @RequestBody Map<String, String> body) {
        return ResponseEntity.ok(orderService.changeStatus(orderId, body.get("status")));
    }

    // ─── DELETE ───────────────────────────────────────────────────────────────

    @Operation(summary = "Delete an order", description = "Permanently deletes an order record. Admin only.")
    @ApiResponse(responseCode = "200", description = "Order deleted")
    @DeleteMapping("/{orderId}")
    public ResponseEntity<String> deleteOrder(
            @Parameter(description = "Order ID", example = "10") @PathVariable Long orderId) {
        orderService.deleteOrder(orderId);
        return ResponseEntity.ok("Order deleted successfully.");
    }

    // ─── ADDRESS ─────────────────────────────────────────────────────────────

    @Operation(summary = "Save a delivery address",
               description = "Saves a delivery address linked to a customer. Called during checkout.")
    @ApiResponse(responseCode = "201", description = "Address saved", content = @Content(schema = @Schema(implementation = Address.class)))
    @PostMapping("/address")
    public ResponseEntity<Address> storeAddress(@RequestBody Address address) {
        return new ResponseEntity<>(orderService.storeAddress(address), HttpStatus.CREATED);
    }

    @Operation(summary = "Get saved addresses by customer", description = "Returns all saved delivery addresses for a customer.")
    @ApiResponse(responseCode = "200", description = "List of addresses")
    @GetMapping("/address/customer/{customerId}")
    public ResponseEntity<List<Address>> getAddressByCustomer(
            @Parameter(description = "Customer (User) ID", example = "1") @PathVariable Long customerId) {
        return ResponseEntity.ok(orderService.getAddressByCustomer(customerId));
    }

    @Operation(summary = "Get all addresses (Admin)", description = "Returns every saved address in the system.")
    @ApiResponse(responseCode = "200", description = "All addresses")
    @GetMapping("/address/all")
    public ResponseEntity<List<Address>> getAllAddresses() {
        return ResponseEntity.ok(orderService.getAllAddresses());
    }

    @Operation(summary = "Delete an address", description = "Removes a saved delivery address.")
    @ApiResponse(responseCode = "200", description = "Address deleted")
    @DeleteMapping("/address/{addressId}")
    public ResponseEntity<String> deleteAddress(
            @Parameter(description = "Address ID", example = "3") @PathVariable Long addressId) {
        orderService.deleteAddress(addressId);
        return ResponseEntity.ok("Address deleted.");
    }
}
