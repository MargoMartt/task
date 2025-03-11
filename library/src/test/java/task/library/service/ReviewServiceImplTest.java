package task.library.service;

import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import task.library.dto.BookRatingResponse;
import task.library.dto.ReviewRequest;
import task.library.entity.Book;
import task.library.entity.Review;
import task.library.exception.NotFoundException;
import task.library.repository.BookRepository;
import task.library.repository.ReviewRepository;
import org.junit.jupiter.api.Test;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.Assert.assertThrows;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ReviewServiceImplTest {

    @Mock
    private ReviewRepository reviewRepository;

    @Mock
    private BookRepository bookRepository;

    @InjectMocks
    private ReviewServiceImpl reviewService;

    @Test
    void testAddReviewToBook_Success() {
        Book book = new Book(1L, "Java Programming", "John Doe", 2023, 5);
        ReviewRequest reviewRequest = new ReviewRequest(5, "Great book!");
        Review review = new Review();
        review.setRating(5);
        review.setComment("Great book!");
        review.setBook(book);

        when(bookRepository.findById(1L)).thenReturn(Optional.of(book));
        when(reviewRepository.save(any(Review.class))).thenReturn(review);

        Review addedReview = reviewService.addReviewToBook(1L, reviewRequest);

        assertNotNull(addedReview);
        assertEquals(5, addedReview.getRating());
        assertEquals("Great book!", addedReview.getComment());

        verify(bookRepository, times(1)).findById(1L);
        verify(reviewRepository, times(1)).save(any(Review.class));
    }

    @Test
    void testAddReviewToBook_NotFound() {
        ReviewRequest reviewRequest = new ReviewRequest(5, "Great book!");

        when(bookRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> reviewService.addReviewToBook(1L, reviewRequest));

        verify(bookRepository, times(1)).findById(1L);
    }

    @Test
    void testGetAllReviewsForBook_Success() {
        Book book = new Book(1L, "Java Programming", "John Doe", 2023, 5);
        List<Review> reviews = List.of(new Review(1L, 5, "Great book!", book));

        when(bookRepository.findById(1L)).thenReturn(Optional.of(book));
        when(reviewRepository.findAllByBook(book)).thenReturn(reviews);

        List<Review> foundReviews = reviewService.getAllReviewsForBook(1L);

        assertNotNull(foundReviews);
        assertEquals(1, foundReviews.size());
        assertEquals(5, foundReviews.get(0).getRating());

        verify(bookRepository, times(1)).findById(1L);
        verify(reviewRepository, times(1)).findAllByBook(book);
    }

    @Test
    void testGetAllReviewsForBook_NotFound() {
        Book book = new Book(1L, "Java Programming", "John Doe", 2023, 5);

        when(bookRepository.findById(1L)).thenReturn(Optional.of(book));
        when(reviewRepository.findAllByBook(book)).thenReturn(Collections.emptyList());

        assertThrows(NotFoundException.class, () -> reviewService.getAllReviewsForBook(1L));

        verify(bookRepository, times(1)).findById(1L);
        verify(reviewRepository, times(1)).findAllByBook(book);
    }

    @Test
    void testUpdateReview_Success() {
        Review existingReview = new Review(1L, 3, "Good", null);
        ReviewRequest reviewRequest = new ReviewRequest(5, "Excellent!");
        Review updatedReview = new Review(1L, 5, "Excellent!", null);

        when(reviewRepository.findById(1L)).thenReturn(Optional.of(existingReview));
        when(reviewRepository.save(any(Review.class))).thenReturn(updatedReview);

        Review result = reviewService.updateReview(1L, reviewRequest);

        assertNotNull(result);
        assertEquals(5, result.getRating());
        assertEquals("Excellent!", result.getComment());

        verify(reviewRepository, times(1)).findById(1L);
        verify(reviewRepository, times(1)).save(any(Review.class));
    }

    @Test
    void testUpdateReview_NotFound() {
        ReviewRequest reviewRequest = new ReviewRequest(5, "Excellent!");

        when(reviewRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> reviewService.updateReview(1L, reviewRequest));

        verify(reviewRepository, times(1)).findById(1L);
    }

    @Test
    void testDeleteReview_Success() {
        when(reviewRepository.existsById(1L)).thenReturn(true);

        reviewService.deleteReview(1L);

        verify(reviewRepository, times(1)).deleteById(1L);
    }

    @Test
    void testDeleteReview_NotFound() {
        when(reviewRepository.existsById(1L)).thenReturn(false);

        assertThrows(NotFoundException.class, () -> reviewService.deleteReview(1L));

        verify(reviewRepository, times(1)).existsById(1L);
    }

    @Test
    void testGetAverageRatingsForBooks_Success() {
        List<BookRatingResponse> ratingsForBooks = List.of(new BookRatingResponse("Java Programming", 4.5));

        when(reviewRepository.getAverageRatingsForBooks()).thenReturn(ratingsForBooks);

        List<BookRatingResponse> foundRatings = reviewService.getAverageRatingsForBooks();

        assertNotNull(foundRatings);
        assertEquals(1, foundRatings.size());
        assertEquals(4.5, foundRatings.get(0).getAverageRating());

        verify(reviewRepository, times(1)).getAverageRatingsForBooks();
    }

    @Test
    void testGetAverageRatingsForBooks_NotFound() {
        when(reviewRepository.getAverageRatingsForBooks()).thenReturn(Collections.emptyList());

        assertThrows(NotFoundException.class, () -> reviewService.getAverageRatingsForBooks());

        verify(reviewRepository, times(1)).getAverageRatingsForBooks();
    }
}
