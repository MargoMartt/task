package task.library.service;

import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import task.library.config.feign.AuthorClient;
import task.library.dto.AuthorDetails;
import task.library.dto.BookDto;
import task.library.dto.BookRequest;
import task.library.entity.Book;
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
class BookServiceImplTest {

    @Mock
    private BookRepository bookRepository;

    @Mock
    private ReviewRepository reviewRepository;

    @Mock
    private AuthorClient authorClient;

    @InjectMocks
    private BookServiceImpl bookService;

    @Test
    void testCreateBook() {
        Book book = new Book(1L, "Java Programming", "John Doe", 2023, 5);

        when(bookRepository.save(book)).thenReturn(book);

        Book createdBook = bookService.createBook(book);

        assertNotNull(createdBook);
        assertEquals(book.getId(), createdBook.getId());
        assertEquals(book.getTitle(), createdBook.getTitle());

        verify(bookRepository, times(1)).save(book);
    }

    @Test
    void testGetAllBooks_Success() {
        List<Book> books = List.of(
                new Book(1L, "Java Programming", "John Doe", 2023, 5),
                new Book(2L, "Spring Boot Essentials", "Jane Smith", 2022, 4)
        );
        when(bookRepository.findAll()).thenReturn(books);

        List<Book> result = bookService.getAllBooks();

        assertEquals(2, result.size());
        verify(bookRepository, times(1)).findAll();
    }

    @Test
    void testGetAllBooks_NotFound() {
        when(bookRepository.findAll()).thenReturn(Collections.emptyList());

        NotFoundException exception = assertThrows(NotFoundException.class, () -> bookService.getAllBooks());
        assertEquals("Books not found", exception.getMessage());

        verify(bookRepository, times(1)).findAll();
    }

    @Test
    void testGetBookById_Success() {
        Book book = new Book(1L, "Java Programming", "John Doe", 2023, 5);

        when(bookRepository.findById(1L)).thenReturn(Optional.of(book));

        Book foundBook = bookService.getBookById(1L);
        assertNotNull(foundBook);
        assertEquals(1L, foundBook.getId());

        verify(bookRepository, times(1)).findById(1L);
    }

    @Test
    void testGetBookById_NotFound() {
        when(bookRepository.findById(1L)).thenReturn(Optional.empty());

        NotFoundException exception = assertThrows(NotFoundException.class, () -> bookService.getBookById(1L));
        assertEquals("Book with ID 1 not found", exception.getMessage());

        verify(bookRepository, times(1)).findById(1L);
    }

    @Test
    void testUpdateBook_Success() {
        Book book = new Book(1L, "Java Programming", "John Doe", 2023, 5);
        BookRequest request = new BookRequest("Advanced Java", "John Doe", 2024, 3);

        when(bookRepository.findById(1L)).thenReturn(Optional.of(book));
        when(bookRepository.save(any(Book.class))).thenReturn(book);

        Book updatedBook = bookService.updateBook(1L, request);

        assertNotNull(updatedBook);
        assertEquals("Advanced Java", updatedBook.getTitle());
        assertEquals(2024, updatedBook.getPublicationYear());

        verify(bookRepository, times(1)).save(any(Book.class));
    }

    @Test
    void testUpdateBook_NotFound() {
        BookRequest request = new BookRequest("Advanced Java", "John Doe", 2024, 3);

        when(bookRepository.findById(1L)).thenReturn(Optional.empty());

        NotFoundException exception = assertThrows(NotFoundException.class, () -> bookService.updateBook(1L, request));
        assertEquals("Book with ID 1 not found", exception.getMessage());

        verify(bookRepository, never()).save(any(Book.class));
    }

    @Test
    void testDeleteBook_Success() {
        when(bookRepository.existsById(1L)).thenReturn(true);
        doNothing().when(bookRepository).deleteById(1L);

        bookService.deleteBook(1L);

        verify(bookRepository, times(1)).deleteById(1L);
    }

    @Test
    void testDeleteBook_NotFound() {
        when(bookRepository.existsById(1L)).thenReturn(false);

        NotFoundException exception = assertThrows(NotFoundException.class, () -> bookService.deleteBook(1L));
        assertEquals("Book with ID 1 not found", exception.getMessage());

        verify(bookRepository, never()).deleteById(anyLong());
    }

    @Test
    void testSearchBooks_Success() {
        List<Book> books = List.of(new Book(1L, "Java Programming", "John Doe", 2023, 5));

        when(bookRepository.findByTitleOrAuthor("Java", "John Doe")).thenReturn(books);

        List<Book> foundBooks = bookService.searchBooks("Java", "John Doe");

        assertNotNull(foundBooks);
        assertEquals(1, foundBooks.size());
        assertEquals("Java Programming", foundBooks.get(0).getTitle());

        verify(bookRepository, times(1)).findByTitleOrAuthor("Java", "John Doe");
    }

    @Test
    void testSearchBooks_NotFound() {
        when(bookRepository.findByTitleOrAuthor("Unknown", "Unknown")).thenReturn(Collections.emptyList());

        assertThrows(NotFoundException.class, () -> bookService.searchBooks("Unknown", "Unknown"));

        verify(bookRepository, times(1)).findByTitleOrAuthor("Unknown", "Unknown");
    }

    @Test
    void testGetBooksPublishedAfterYear_Success() {
        List<Book> books = List.of(new Book(1L, "Advanced Java", "John Doe", 2021, 3));

        when(bookRepository.findBooksPublishedAfterYear(2020)).thenReturn(books);

        List<Book> foundBooks = bookService.getBooksPublishedAfterYear(2020);

        assertNotNull(foundBooks);
        assertEquals(1, foundBooks.size());
        assertEquals(2021, foundBooks.get(0).getPublicationYear());

        verify(bookRepository, times(1)).findBooksPublishedAfterYear(2020);
    }

    @Test
    void testGetBooksPublishedAfterYear_NotFound() {
        when(bookRepository.findBooksPublishedAfterYear(2030)).thenReturn(Collections.emptyList());

        assertThrows(NotFoundException.class, () -> bookService.getBooksPublishedAfterYear(2030));

        verify(bookRepository, times(1)).findBooksPublishedAfterYear(2030);
    }

    @Test
    void testGetBooksWithHighRatingsNativeSQL_Success() {
        List<Book> books = List.of(new Book(1L, "Advanced Java", "John Doe", 2008, 10));

        when(bookRepository.getBooksWithHighRatingsNativeSQL()).thenReturn(books);

        List<Book> foundBooks = bookService.getBooksWithHighRatingsNativeSQL();

        assertNotNull(foundBooks);
        assertEquals(1, foundBooks.size());
        assertEquals("Advanced Java", foundBooks.get(0).getTitle());

        verify(bookRepository, times(1)).getBooksWithHighRatingsNativeSQL();
    }

    @Test
    void testGetBooksWithHighRatingsNativeSQL_NotFound() {
        when(bookRepository.getBooksWithHighRatingsNativeSQL()).thenReturn(Collections.emptyList());

        assertThrows(NotFoundException.class, () -> bookService.getBooksWithHighRatingsNativeSQL());

        verify(bookRepository, times(1)).getBooksWithHighRatingsNativeSQL();
    }

    @Test
    void testGetBooksWithHighRatingsJPQL_Success() {
        List<Book> books = List.of(new Book(1L, "Advanced Java", "John Doe", 2018, 8));

        when(reviewRepository.getBooksWithHighRatingsJPQL()).thenReturn(books);

        List<Book> foundBooks = bookService.getBooksWithHighRatingsJPQL();

        assertNotNull(foundBooks);
        assertEquals(1, foundBooks.size());
        assertEquals("Advanced Java", foundBooks.get(0).getTitle());

        verify(reviewRepository, times(1)).getBooksWithHighRatingsJPQL();
    }

    @Test
    void testGetBooksWithHighRatingsJPQL_NotFound() {
        when(reviewRepository.getBooksWithHighRatingsJPQL()).thenReturn(Collections.emptyList());

        assertThrows(NotFoundException.class, () -> bookService.getBooksWithHighRatingsJPQL());

        verify(reviewRepository, times(1)).getBooksWithHighRatingsJPQL();
    }

    @Test
    void testGetBookWithAuthorDetails_Success() {
        Book book = new Book(1L, "Advanced Java", "John Doe", 2023, 5);
        AuthorDetails authorDetails = new AuthorDetails("A renowned Java developer...", "American");

        when(bookRepository.findById(1L)).thenReturn(Optional.of(book));
        when(authorClient.getAuthorDetails("John Doe")).thenReturn(authorDetails);

        BookDto result = bookService.getBookWithAuthorDetails(1L);

        assertNotNull(result);
        assertEquals("A renowned Java developer...", result.getAuthorDetails().getBiography());

        verify(authorClient, times(1)).getAuthorDetails("John Doe");
    }

    @Test
    void testGetBookWithAuthorDetails_AuthorNotFound() {
        Book book = new Book(1L, "Advanced Java", "John Doe", 2023, 5);

        when(bookRepository.findById(1L)).thenReturn(Optional.of(book));
        when(authorClient.getAuthorDetails("John Doe")).thenReturn(null);

        NotFoundException exception = assertThrows(NotFoundException.class, () -> bookService.getBookWithAuthorDetails(1L));
        assertEquals("Author not found", exception.getMessage());

        verify(authorClient, times(1)).getAuthorDetails("John Doe");
    }

    @Test
    void testGetBookWithAuthorDetails_NotFoundBook() {
        when(bookRepository.findById(1L)).thenReturn(Optional.empty());

        NotFoundException exception = assertThrows(NotFoundException.class, () -> bookService.getBookWithAuthorDetails(1L));
        assertEquals("Book with ID 1 not found", exception.getMessage());
    }


}
