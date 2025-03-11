package task.library.service;

import task.library.dto.BookRatingResponse;
import task.library.dto.ReviewRequest;
import task.library.entity.Review;

import java.util.List;

public interface ReviewService {

    Review addReviewToBook(Long bookId, ReviewRequest reviewRequest);

    List<Review> getAllReviewsForBook(Long bookId);

    Review updateReview(Long reviewId, ReviewRequest reviewRequest);

    void deleteReview(Long reviewId);

    List<BookRatingResponse> getAverageRatingsForBooks();
}
