package task.library.integtation;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.transaction.annotation.Transactional;
import task.library.dto.BookRequest;
import task.library.entity.Book;
import task.library.dto.BookDto;
import task.library.dto.AuthorDetails;
import task.library.entity.Review;
import task.library.repository.BookRepository;
import task.library.repository.ReviewRepository;
import task.library.config.feign.AuthorClient;
import task.library.exception.NotFoundException;
import task.library.service.BookServiceImpl;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.List;

@SpringBootTest
@Transactional
public class BookServiceImplIntegrationTest {

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private ReviewRepository reviewRepository;

    @MockBean
    private AuthorClient authorClient;

    @Autowired
    private BookServiceImpl bookService;

    private Book book1;
    private Book book2;
    private Review review;

    @BeforeEach
    public void setUp() {
        book1 = new Book(null, "Java Programming", "John Doe", 2023, 5);
        book2 = new Book(null, "Spring Boot", "Jane Doe", 2024, 10);
        review = new Review(1L, 5, "Great", book1);
        book1 = bookRepository.save(book1);
        book2 = bookRepository.save(book2);
        review = reviewRepository.save(review);
    }

    @Test
    public void testCreateBook_Success() {
        Book newBook = new Book(null, "Spring Boot", "Jane Doe", 2024, 10);
        Book savedBook = bookService.createBook(newBook);

        assertNotNull(savedBook);
        assertNotNull(savedBook.getId());
        assertEquals("Spring Boot", savedBook.getTitle());
    }

    @Test
    public void testGetAllBooks_Success() {
        List<Book> books = bookService.getAllBooks();

        assertNotNull(books);
        assertTrue(books.size() > 0);
    }

    @Test
    public void testGetAllBooks_Empty() {
        reviewRepository.deleteAll();
        bookRepository.deleteAll();

        NotFoundException exception = assertThrows(NotFoundException.class, () -> bookService.getAllBooks());
        assertEquals("Books not found", exception.getMessage());
    }

    @Test
    public void testGetBookById_Success() {
        Book foundBook = bookService.getBookById(book1.getId());

        assertNotNull(foundBook);
        assertEquals(book1.getId(), foundBook.getId());
        assertEquals(book1.getTitle(), foundBook.getTitle());
    }

    @Test
    public void testGetBookById_NotFound() {
        NotFoundException exception = assertThrows(NotFoundException.class, () -> bookService.getBookById(999L));
        assertEquals("Book with ID 999 not found", exception.getMessage());
    }

    @Test
    public void testUpdateBook_Success() {
        BookRequest request = new BookRequest("Updated Title", "Updated Author", 2025, 15);
        Book updatedBook = bookService.updateBook(book1.getId(), request);

        assertNotNull(updatedBook);
        assertEquals("Updated Title", updatedBook.getTitle());
        assertEquals("Updated Author", updatedBook.getAuthor());
    }

    @Test
    public void testUpdateBook_NotFound() {
        BookRequest request = new BookRequest("Updated Title", "Updated Author", 2025, 15);

        NotFoundException exception = assertThrows(NotFoundException.class, () -> bookService.updateBook(999L, request));
        assertEquals("Book with ID 999 not found", exception.getMessage());
    }

    @Test
    public void testDeleteBook_Success() {
        bookService.deleteBook(book2.getId());
        assertFalse(bookRepository.existsById(book2.getId()));
    }

    @Test
    public void testDeleteBook_NotFound() {
        NotFoundException exception = assertThrows(NotFoundException.class, () -> bookService.deleteBook(999L));
        assertEquals("Book with ID 999 not found", exception.getMessage());
    }

    @Test
    public void testSearchBooks_Success() {
        List<Book> books = bookService.searchBooks("Java", "John Doe");

        assertNotNull(books);
        assertTrue(books.size() > 0);
    }

    @Test
    public void testSearchBooks_NotFound() {
        NotFoundException exception = assertThrows(NotFoundException.class, () -> bookService.searchBooks("Unknown Title", "Unknown Author"));
        assertEquals("No books found for the given title 'Unknown Title' or author 'Unknown Author'", exception.getMessage());
    }

    @Test
    public void testGetBooksPublishedAfterYear_Success() {
        List<Book> books = bookService.getBooksPublishedAfterYear(2020);

        assertNotNull(books);
        assertTrue(books.size() > 0);
    }

    @Test
    public void testGetBooksPublishedAfterYear_NotFound() {
        NotFoundException exception = assertThrows(NotFoundException.class, () -> bookService.getBooksPublishedAfterYear(2025));
        assertEquals("Book after 2025 year wasn't published", exception.getMessage());
    }

    @Test
    public void testGetBooksWithHighRatingsNativeSQL_Success() {
        List<Book> books = bookService.getBooksWithHighRatingsNativeSQL();

        assertNotNull(books);
        assertTrue(books.size() > 0);
    }

    @Test
    public void testGetBooksWithHighRatingsNativeSQL_NotFound() {
        review.setRating(3);
        reviewRepository.save(review);

        NotFoundException exception = assertThrows(NotFoundException.class, () -> bookService.getBooksWithHighRatingsNativeSQL());
        assertEquals("No book has a rating hire than Four", exception.getMessage());
    }

    @Test
    public void testGetBooksWithHighRatingsJPQL_Success() {
        List<Book> books = bookService.getBooksWithHighRatingsJPQL();

        assertNotNull(books);
        assertTrue(books.size() > 0);
    }

    @Test
    public void testGetBooksWithHighRatingsJPQL_NotFound() {
        review.setRating(3);
        reviewRepository.save(review);

        NotFoundException exception = assertThrows(NotFoundException.class, () -> bookService.getBooksWithHighRatingsJPQL());
        assertEquals("No book has a rating hire than Four", exception.getMessage());
    }

    @Test
    public void testGetBookWithAuthorDetails_Success() {
        AuthorDetails authorDetails = new AuthorDetails("A renowned Java developer...", "American");
        when(authorClient.getAuthorDetails(book1.getAuthor())).thenReturn(authorDetails);

        BookDto bookDto = bookService.getBookWithAuthorDetails(book1.getId());

        assertNotNull(bookDto);
        assertEquals(book1.getTitle(), bookDto.getTitle());
        assertEquals(book1.getAuthor(), bookDto.getAuthor());
        assertNotNull(bookDto.getAuthorDetails());
    }

    @Test
    public void testGetBookWithAuthorDetails_AuthorNotFound() {
        when(authorClient.getAuthorDetails(book2.getAuthor())).thenReturn(null);

        NotFoundException exception = assertThrows(NotFoundException.class, () -> bookService.getBookWithAuthorDetails(book2.getId()));
        assertEquals("Author not found", exception.getMessage());
    }
}
