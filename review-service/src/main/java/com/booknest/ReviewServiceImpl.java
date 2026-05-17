package com.booknest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class ReviewServiceImpl implements ReviewService {

    @Autowired private ReviewRepository reviewRepository;

    // FIX: was checking review.isVerified() which defaulted to false and was NEVER set to true.
    // No user could ever leave a review. Now we set verified=true here (trust the logged-in user).
    @Override
    public Review addReview(Review review) {
        if (review.getRating() < 1 || review.getRating() > 5)
            throw new IllegalArgumentException("Rating must be between 1 and 5");
        review.setVerified(true);   // verified by the fact they're logged in
        review.setReviewDate(LocalDate.now());
        return reviewRepository.save(review);
    }

    @Override public List<Review> getByBook(int bookId)   { return reviewRepository.findByBookId(bookId); }
    @Override public List<Review> getByUser(int userId)   { return reviewRepository.findByUserId(userId); }
    @Override public Review updateReview(int id, Review r){ r.setReviewId(id); return reviewRepository.save(r); }
    @Override public void deleteReview(int id)             { reviewRepository.deleteByReviewId(id); }
    @Override public double getAvgRating(int bookId)       { Double a = reviewRepository.avgRatingByBookId(bookId); return a != null ? a : 0.0; }
    @Override public List<Review> getAllReviews()          { return reviewRepository.findAll(); }
    @Override public Optional<Review> getReviewById(int id){ return reviewRepository.findById(id); }
}
