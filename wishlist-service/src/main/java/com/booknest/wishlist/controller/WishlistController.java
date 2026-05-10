package com.booknest.wishlist.controller;

import com.booknest.wishlist.entity.Wishlist;
import com.booknest.wishlist.service.WishlistService;
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
@RequestMapping("/wishlist")
@Tag(name = "Wishlist", description = "Save books for later — add, remove, clear, and move items to shopping cart")
@SecurityRequirement(name = "BearerAuth")
public class WishlistController {

    @Autowired
    private WishlistService wishlistService;

    @Operation(summary = "Get wishlist for a user",
               description = "Returns the user's wishlist with all saved books. Auto-creates an empty wishlist if none exists.")
    @ApiResponse(responseCode = "200", description = "Wishlist returned", content = @Content(schema = @Schema(implementation = Wishlist.class)))
    @GetMapping("/{userId}")
    public ResponseEntity<Wishlist> getWishlist(
            @Parameter(description = "User ID", example = "1") @PathVariable Long userId) {
        return ResponseEntity.ok(wishlistService.getWishlistByUser(userId));
    }

    @Operation(summary = "Add a book to wishlist",
               description = "Saves a book to the user's wishlist for future purchase.\n\n" +
                             "Body: `{ \"bookId\": 1, \"bookTitle\": \"Clean Code\", \"bookPrice\": 499.0 }`")
    @ApiResponse(responseCode = "201", description = "Book added to wishlist", content = @Content(schema = @Schema(implementation = Wishlist.class)))
    @PostMapping("/{userId}/add")
    public ResponseEntity<Wishlist> addBook(
            @Parameter(description = "User ID", example = "1") @PathVariable Long userId,
            @RequestBody Map<String, Object> body) {

        Long bookId     = Long.valueOf(body.get("bookId").toString());
        String bookTitle = body.get("bookTitle").toString();
        double bookPrice = Double.parseDouble(body.get("bookPrice").toString());

        return new ResponseEntity<>(wishlistService.addBook(userId, bookId, bookTitle, bookPrice), HttpStatus.CREATED);
    }

    @Operation(summary = "Remove a book from wishlist",
               description = "Removes a specific book from the user's wishlist by bookId.")
    @ApiResponse(responseCode = "200", description = "Book removed, updated wishlist returned")
    @DeleteMapping("/{userId}/remove/{bookId}")
    public ResponseEntity<Wishlist> removeBook(
            @Parameter(description = "User ID", example = "1") @PathVariable Long userId,
            @Parameter(description = "Book ID to remove", example = "1") @PathVariable Long bookId) {
        return ResponseEntity.ok(wishlistService.removeBook(userId, bookId));
    }

    @Operation(summary = "Clear wishlist",
               description = "Removes all items from the user's wishlist.")
    @ApiResponse(responseCode = "200", description = "Wishlist cleared")
    @DeleteMapping("/{userId}/clear")
    public ResponseEntity<String> clearWishlist(
            @Parameter(description = "User ID", example = "1") @PathVariable Long userId) {
        wishlistService.clearWishlist(userId);
        return ResponseEntity.ok("Wishlist cleared for userId: " + userId);
    }

    @Operation(summary = "Get all wishlists (Admin)", description = "Returns wishlists for all users. Admin only.")
    @ApiResponse(responseCode = "200", description = "All wishlists")
    @GetMapping("/all")
    public ResponseEntity<List<Wishlist>> getAllWishlists() {
        return ResponseEntity.ok(wishlistService.getAllWishlists());
    }
}
