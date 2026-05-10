package com.booknest.review.service;

import com.booknest.review.entity.Review;

import java.util.List;
import java.util.Optional;

public interface ReviewService {

    Review addReview(Review review);

    List<Review> getByBook(Long bookId);

    List<Review> getByUser(Long userId);

    Optional<Review> getReviewById(Long reviewId);

    Review updateReview(Long reviewId, Review updatedReview);

    void deleteReview(Long reviewId);

    Double getAvgRating(Long bookId);

    long getReviewCount(Long bookId);

    List<Review> getAllReviews();
}
