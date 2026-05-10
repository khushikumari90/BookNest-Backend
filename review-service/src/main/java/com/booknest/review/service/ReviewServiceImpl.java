package com.booknest.review.service;

import com.booknest.review.entity.Review;
import com.booknest.review.exception.ReviewNotFoundException;
import com.booknest.review.repository.ReviewRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ReviewServiceImpl implements ReviewService {

    @Autowired
    private ReviewRepository reviewRepository;

    @Override
    public Review addReview(Review review) {
        // Rating must be 1-5
        if (review.getRating() < 1 || review.getRating() > 5) {
            throw new RuntimeException("Rating must be between 1 and 5.");
        }
        // One review per user per book
        reviewRepository.findByBookIdAndUserId(review.getBookId(), review.getUserId())
                .ifPresent(r -> {
                    throw new RuntimeException(
                            "You have already reviewed this book. Please edit your existing review.");
                });
        return reviewRepository.save(review);
    }

    @Override
    public List<Review> getByBook(Long bookId) {
        return reviewRepository.findByBookId(bookId);
    }

    @Override
    public List<Review> getByUser(Long userId) {
        return reviewRepository.findByUserId(userId);
    }

    @Override
    public Optional<Review> getReviewById(Long reviewId) {
        return reviewRepository.findById(reviewId);
    }

    @Override
    public Review updateReview(Long reviewId, Review updatedReview) {
        Review existing = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new ReviewNotFoundException(
                        "Review not found with id: " + reviewId));

        if (updatedReview.getRating() < 1 || updatedReview.getRating() > 5) {
            throw new RuntimeException("Rating must be between 1 and 5.");
        }

        existing.setRating(updatedReview.getRating());
        existing.setComment(updatedReview.getComment());
        return reviewRepository.save(existing);
    }

    @Override
    public void deleteReview(Long reviewId) {
        if (!reviewRepository.existsById(reviewId)) {
            throw new ReviewNotFoundException("Review not found with id: " + reviewId);
        }
        reviewRepository.deleteById(reviewId);
    }

    @Override
    public Double getAvgRating(Long bookId) {
        Double avg = reviewRepository.avgRatingByBookId(bookId);
        return avg != null ? Math.round(avg * 10.0) / 10.0 : 0.0;
    }

    @Override
    public long getReviewCount(Long bookId) {
        return reviewRepository.countByBookId(bookId);
    }

    @Override
    public List<Review> getAllReviews() {
        return reviewRepository.findAll();
    }
}
