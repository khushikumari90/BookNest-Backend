package com.booknest.cart.controller;

import com.booknest.cart.entity.Cart;
import com.booknest.cart.service.CartService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@CrossOrigin(origins = "*", allowedHeaders = "*")
@RestController
@RequestMapping("/cart")
@Tag(name = "Cart", description = "Shopping cart — add, remove, update items and compute totals")
@SecurityRequirement(name = "BearerAuth")
public class CartController {

    @Autowired
    private CartService cartService;

    @Operation(summary = "Get cart for a user",
               description = "Returns the cart with all items and totalPrice. Auto-creates empty cart if none exists.")
    @ApiResponse(responseCode = "200", description = "Cart returned", content = @Content(schema = @Schema(implementation = Cart.class)))
    @GetMapping("/{userId}")
    public ResponseEntity<Cart> getCart(
            @Parameter(description = "User ID", example = "1") @PathVariable Long userId) {
        return ResponseEntity.ok(cartService.getCartByUser(userId));
    }

    @Operation(summary = "Add item to cart",
               description = "Adds a book to cart. If already present, quantity is incremented.\n\nBody: `{ \"bookId\": 1, \"bookTitle\": \"Clean Code\", \"price\": 499.0, \"quantity\": 2 }`")
    @ApiResponse(responseCode = "201", description = "Item added, updated cart returned")
    @PostMapping("/{userId}/add")
    public ResponseEntity<Cart> addItem(
            @Parameter(description = "User ID", example = "1") @PathVariable Long userId,
            @RequestBody Map<String, Object> body) {
        Long bookId  = Long.valueOf(body.get("bookId").toString());
        String title = body.get("bookTitle").toString();
        double price = Double.parseDouble(body.get("price").toString());
        int qty      = Integer.parseInt(body.get("quantity").toString());
        return new ResponseEntity<>(cartService.addItem(userId, bookId, title, price, qty), HttpStatus.CREATED);
    }

    @Operation(summary = "Remove an item from cart", description = "Removes a single CartItem by itemId.")
    @ApiResponse(responseCode = "200", description = "Item removed, updated cart returned")
    @DeleteMapping("/{userId}/remove/{itemId}")
    public ResponseEntity<Cart> removeItem(
            @Parameter(description = "User ID", example = "1") @PathVariable Long userId,
            @Parameter(description = "Cart Item ID", example = "5") @PathVariable Long itemId) {
        return ResponseEntity.ok(cartService.removeItem(userId, itemId));
    }

    @Operation(summary = "Update item quantity", description = "Body: `{ \"quantity\": 3 }`")
    @ApiResponse(responseCode = "200", description = "Quantity updated, cart returned")
    @PutMapping("/{userId}/update/{itemId}")
    public ResponseEntity<Cart> updateQuantity(
            @Parameter(description = "User ID", example = "1") @PathVariable Long userId,
            @Parameter(description = "Cart Item ID", example = "5") @PathVariable Long itemId,
            @RequestBody Map<String, Integer> body) {
        return ResponseEntity.ok(cartService.updateQuantity(userId, itemId, body.get("quantity")));
    }

    @Operation(summary = "Clear cart", description = "Removes all items. Called after successful order placement.")
    @ApiResponse(responseCode = "200", description = "Cart cleared")
    @DeleteMapping("/{userId}/clear")
    public ResponseEntity<String> clearCart(
            @Parameter(description = "User ID", example = "1") @PathVariable Long userId) {
        cartService.clearCart(userId);
        return ResponseEntity.ok("Cart cleared for userId: " + userId);
    }

    @Operation(summary = "Get cart total", description = "Returns only the total price as a double value in INR.")
    @ApiResponse(responseCode = "200", description = "Total price in INR")
    @GetMapping("/{userId}/total")
    public ResponseEntity<Double> getCartTotal(
            @Parameter(description = "User ID", example = "1") @PathVariable Long userId) {
        return ResponseEntity.ok(cartService.cartTotal(userId));
    }

    @Operation(summary = "Get all carts (Admin)", description = "Returns carts for all users. Admin only.")
    @ApiResponse(responseCode = "200", description = "All carts list")
    @GetMapping("/all")
    public ResponseEntity<List<Cart>> getAllCarts() {
        return ResponseEntity.ok(cartService.getAllCarts());
    }
}
