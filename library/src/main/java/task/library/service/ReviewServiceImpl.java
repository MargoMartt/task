package task.library.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import task.library.dto.BookRatingResponse;
import task.library.dto.ReviewRequest;
import task.library.entity.Book;
import task.library.entity.Review;
import task.library.exception.NotFoundException;
import task.library.repository.ReviewRepository;
import task.library.repository.BookRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ReviewServiceImpl implements ReviewService {

    private final ReviewRepository reviewRepository;
    private final BookRepository bookRepository;

    public Review addReviewToBook(Long bookId, ReviewRequest reviewRequest) {
        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> NotFoundException.notFoundBook(bookId));
        Review review = new Review();
        review.setRating(reviewRequest.getRating());
        review.setComment(reviewRequest.getComment());
        review.setBook(book);
        return reviewRepository.save(review);

    }

    public List<Review> getAllReviewsForBook(Long bookId) {
        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> NotFoundException.notFoundBook(bookId));
        List<Review> reviews = reviewRepository.findAllByBook(book);
        if (reviews.isEmpty()) {
            throw NotFoundException.notFoundReviewsForBook(bookId);
        }
        return reviews;
    }

    public Review updateReview(Long reviewId, ReviewRequest reviewRequest) {
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> NotFoundException.notFoundReview(reviewId));
        review.setRating(reviewRequest.getRating());
        review.setComment(reviewRequest.getComment());
        return reviewRepository.save(review);
    }

    public void deleteReview(Long reviewId) {
        if (!reviewRepository.existsById(reviewId)) {
            throw NotFoundException.notFoundReview(reviewId);
        }
        reviewRepository.deleteById(reviewId);
    }

    public List<BookRatingResponse> getAverageRatingsForBooks() {
        List<BookRatingResponse> ratingsForBooks = reviewRepository.getAverageRatingsForBooks();
        if (ratingsForBooks.isEmpty()) {
            throw NotFoundException.notFoundAnyRatingForAnyBook();
        }
        return ratingsForBooks;
    }

}

