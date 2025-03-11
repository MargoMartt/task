package task.library.integtation;

import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import task.library.dto.BookRatingResponse;
import task.library.dto.ReviewRequest;
import task.library.entity.Book;
import task.library.entity.Review;
import task.library.repository.BookRepository;
import task.library.repository.ReviewRepository;
import task.library.exception.NotFoundException;
import task.library.service.ReviewServiceImpl;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
public class ReviewServiceImplIntegrationTest {

    @Autowired
    private ReviewServiceImpl reviewService;

    @Autowired
    private ReviewRepository reviewRepository;

    @Autowired
    private BookRepository bookRepository;

    private Book book;
    private ReviewRequest reviewRequest;

    @BeforeEach
    public void setUp() {
        book = new Book(null, "Java Programming", "John Doe", 2023, 5);
        book = bookRepository.save(book);

        reviewRequest = new ReviewRequest(5, "Great book!");
    }

    @Test
    public void testAddReviewToBook_Success() {
        Review review = reviewService.addReviewToBook(book.getId(), reviewRequest);

        assertNotNull(review);
        assertEquals(book.getId(), review.getBook().getId());
        assertEquals(reviewRequest.getRating(), review.getRating());
        assertEquals(reviewRequest.getComment(), review.getComment());
    }

    @Test
    public void testAddReviewToBook_BookNotFound() {
        Long nonExistentBookId = 999L;
        NotFoundException exception = assertThrows(NotFoundException.class, () ->
                reviewService.addReviewToBook(nonExistentBookId, reviewRequest));
        assertEquals("Book with ID 999 not found", exception.getMessage());
    }

    @Test
    public void testGetAllReviewsForBook_Success() {
        reviewService.addReviewToBook(book.getId(), reviewRequest);

        List<Review> reviews = reviewService.getAllReviewsForBook(book.getId());

        assertNotNull(reviews);
        assertTrue(reviews.size() > 0);
    }

    @Test
    public void testGetAllReviewsForBook_NotFoundBook() {
        Long nonExistentBookId = 999L;
        NotFoundException exception = assertThrows(NotFoundException.class, () ->
                reviewService.getAllReviewsForBook(nonExistentBookId));
        assertEquals("Book with ID 999 not found", exception.getMessage());
    }

    @Test
    public void testGetAllReviewsForBook_NotFoundReview() {
        reviewRepository.deleteAll();
        Long bookId = 1L;
        NotFoundException exception = assertThrows(NotFoundException.class, () ->
                reviewService.getAllReviewsForBook(bookId));
        assertEquals("Reviews for book with ID 1 not found", exception.getMessage());
    }

    @Test
    public void testUpdateReview_Success() {
        Review review = reviewService.addReviewToBook(book.getId(), reviewRequest);

        ReviewRequest updatedReviewRequest = new ReviewRequest(4, "Good book!");
        Review updatedReview = reviewService.updateReview(review.getId(), updatedReviewRequest);

        assertEquals(updatedReviewRequest.getRating(), updatedReview.getRating());
        assertEquals(updatedReviewRequest.getComment(), updatedReview.getComment());
    }

    @Test
    public void testUpdateReview_NotFound() {
        Long nonExistentReviewId = 999L;
        ReviewRequest updatedReviewRequest = new ReviewRequest(4, "Updated review");

        NotFoundException exception = assertThrows(NotFoundException.class, () ->
                reviewService.updateReview(nonExistentReviewId, updatedReviewRequest));
        assertEquals("Review with ID 999 not found", exception.getMessage());
    }

    @Test
    public void testDeleteReview_Success() {
        Review review = reviewService.addReviewToBook(book.getId(), reviewRequest);

        reviewService.deleteReview(review.getId());

        assertFalse(reviewRepository.existsById(review.getId()));
    }

    @Test
    public void testDeleteReview_NotFound() {
        Long nonExistentReviewId = 999L;
        NotFoundException exception = assertThrows(NotFoundException.class, () ->
                reviewService.deleteReview(nonExistentReviewId));
        assertEquals("Review with ID 999 not found", exception.getMessage());
    }

    @Test
    public void testGetAverageRatingsForBooks_Success() {
        reviewService.addReviewToBook(book.getId(), reviewRequest);
        ReviewRequest reviewRequest2 = new ReviewRequest(4, "Good book!");
        reviewService.addReviewToBook(book.getId(), reviewRequest2);

        List<BookRatingResponse> ratings = reviewService.getAverageRatingsForBooks();

        assertNotNull(ratings);
        assertTrue(ratings.size() > 0);
    }

    @Test
    public void testGetAverageRatingsForBooks_NotFound() {
        reviewRepository.deleteAll();

        NotFoundException exception = assertThrows(NotFoundException.class, () ->
                reviewService.getAverageRatingsForBooks());
        assertEquals("No book has a rating", exception.getMessage());
    }
}
