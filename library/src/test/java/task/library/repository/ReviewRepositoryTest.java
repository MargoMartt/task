package task.library.repository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import task.library.dto.BookRatingResponse;
import task.library.entity.Book;
import task.library.entity.Review;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(SpringExtension.class)
@DataJpaTest
public class ReviewRepositoryTest {

    @Autowired
    private ReviewRepository reviewRepository;

    @Autowired
    private BookRepository bookRepository;

    private Book book1;
    private Book book2;
    private Review review1;
    private Review review2;

    @BeforeEach
    public void setUp() {
        book1 = new Book(null, "Java Programming", "John Doe", 2023, 5);
        book2 = new Book(null, "Spring Boot", "Jane Doe", 2024, 10);

        review1 = new Review(null, 5, "Excellent book", book1);
        review2 = new Review(null, 4, "Good book", book2);

        bookRepository.save(book1);
        bookRepository.save(book2);
        reviewRepository.save(review1);
        reviewRepository.save(review2);
    }

    @Test
    public void testGetBooksWithHighRatingsJPQL_Success() {
        List<Book> books = reviewRepository.getBooksWithHighRatingsJPQL();

        assertNotNull(books);
        assertTrue(books.size() > 0);
        assertTrue(books.contains(book1));
    }

    @Test
    public void testGetBooksWithHighRatingsJPQL_NoBooksAboveThreshold() {
        reviewRepository.deleteAll();

        List<Book> books = reviewRepository.getBooksWithHighRatingsJPQL();

        assertNotNull(books);
        assertTrue(books.isEmpty(), "The book list is empty");
    }

    @Test
    public void testGetBooksWithHighRatingsJPQL_NoReviewsForBook() {
        reviewRepository.deleteAll();

        List<Book> books = reviewRepository.getBooksWithHighRatingsJPQL();

        assertNotNull(books);
        assertTrue(books.isEmpty(), "The book list is empty because of no rating");
    }

    @Test
    public void testGetAverageRatingsForBooks_Success() {
        List<BookRatingResponse> ratings = reviewRepository.getAverageRatingsForBooks();

        assertNotNull(ratings);
        assertEquals(2, ratings.size());
        assertEquals(5.0, ratings.get(0).getAverageRating());
        assertEquals(4.0, ratings.get(1).getAverageRating());
    }

    @Test
    public void testGetAverageRatingsForBooks_NoReviewsForBook() {
        reviewRepository.deleteAll();
        List<BookRatingResponse> ratings = reviewRepository.getAverageRatingsForBooks();

        assertNotNull(ratings);
        assertTrue(ratings.isEmpty(), "The list is empty because of no rating");

    }
}
