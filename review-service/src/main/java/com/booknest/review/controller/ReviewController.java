package com.booknest.review.controller;

import com.booknest.review.entity.Review;
import com.booknest.review.service.ReviewService;
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

@CrossOrigin(origins = "*", allowedHeaders = "*")
@RestController
@RequestMapping("/reviews")
@Tag(name = "Reviews & Ratings", description = "Book reviews (1-5 stars), average rating aggregation, and admin moderation")
@SecurityRequirement(name = "BearerAuth")
public class ReviewController {

    @Autowired
    private ReviewService reviewService;

    @Operation(summary = "Add a book review",
               description = "Creates a review for a purchased book. Only verified purchasers can submit reviews.\n\n" +
                             "Body must include: `bookId`, `userId`, `rating` (1-5), `comment`, `verified` (boolean).")
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "Review created", content = @Content(schema = @Schema(implementation = Review.class))),
        @ApiResponse(responseCode = "400", description = "Invalid rating or missing fields", content = @Content)
    })
    @PostMapping
    public ResponseEntity<Review> addReview(@RequestBody Review review) {
        return new ResponseEntity<>(reviewService.addReview(review), HttpStatus.CREATED);
    }

    @Operation(summary = "Get all reviews for a book",
               description = "Returns all customer reviews for a specific book, sorted by most recent.")
    @ApiResponse(responseCode = "200", description = "List of reviews for the book")
    @GetMapping("/book/{bookId}")
    public ResponseEntity<List<Review>> getByBook(
            @Parameter(description = "Book ID", example = "1") @PathVariable Long bookId) {
        return ResponseEntity.ok(reviewService.getByBook(bookId));
    }

    @Operation(summary = "Get all reviews by a user",
               description = "Returns all reviews submitted by a specific user.")
    @ApiResponse(responseCode = "200", description = "List of reviews by the user")
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<Review>> getByUser(
            @Parameter(description = "User ID", example = "1") @PathVariable Long userId) {
        return ResponseEntity.ok(reviewService.getByUser(userId));
    }

    @Operation(summary = "Get a single review by ID")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Review found", content = @Content(schema = @Schema(implementation = Review.class))),
        @ApiResponse(responseCode = "404", description = "Review not found", content = @Content)
    })
    @GetMapping("/{reviewId}")
    public ResponseEntity<Review> getById(
            @Parameter(description = "Review ID", example = "1") @PathVariable Long reviewId) {
        return reviewService.getReviewById(reviewId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @Operation(summary = "Get all reviews (Admin)", description = "Returns every review in the platform. Used for admin moderation.")
    @ApiResponse(responseCode = "200", description = "All reviews")
    @GetMapping("/all")
    public ResponseEntity<List<Review>> getAllReviews() {
        return ResponseEntity.ok(reviewService.getAllReviews());
    }

    @Operation(summary = "Get average rating for a book",
               description = "Returns the average star rating (1.0-5.0) computed from all verified reviews.")
    @ApiResponse(responseCode = "200", description = "Average rating as double (e.g., 4.3)")
    @GetMapping("/book/{bookId}/avg-rating")
    public ResponseEntity<Double> getAvgRating(
            @Parameter(description = "Book ID", example = "1") @PathVariable Long bookId) {
        return ResponseEntity.ok(reviewService.getAvgRating(bookId));
    }

    @Operation(summary = "Get review count for a book",
               description = "Returns the total number of reviews for a book.")
    @ApiResponse(responseCode = "200", description = "Review count as long")
    @GetMapping("/book/{bookId}/count")
    public ResponseEntity<Long> getReviewCount(
            @Parameter(description = "Book ID", example = "1") @PathVariable Long bookId) {
        return ResponseEntity.ok(reviewService.getReviewCount(bookId));
    }

    @Operation(summary = "Update a review",
               description = "Allows a user to edit their existing review comment and rating.")
    @ApiResponse(responseCode = "200", description = "Review updated", content = @Content(schema = @Schema(implementation = Review.class)))
    @PutMapping("/{reviewId}")
    public ResponseEntity<Review> updateReview(
            @Parameter(description = "Review ID", example = "1") @PathVariable Long reviewId,
            @RequestBody Review review) {
        return ResponseEntity.ok(reviewService.updateReview(reviewId, review));
    }

    @Operation(summary = "Delete a review",
               description = "Permanently removes a review. Users can delete their own; Admins can delete any.")
    @ApiResponse(responseCode = "200", description = "Review deleted")
    @DeleteMapping("/{reviewId}")
    public ResponseEntity<String> deleteReview(
            @Parameter(description = "Review ID", example = "1") @PathVariable Long reviewId) {
        reviewService.deleteReview(reviewId);
        return ResponseEntity.ok("Review deleted successfully.");
    }
}
