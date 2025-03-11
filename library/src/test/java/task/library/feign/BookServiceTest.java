package task.library.feign;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import task.library.config.feign.AuthorClient;
import task.library.dto.AuthorDetails;
import task.library.dto.BookDto;
import task.library.entity.Book;
import task.library.exception.NotFoundException;
import task.library.repository.BookRepository;
import task.library.service.BookService;
import java.util.Optional;

import static org.junit.Assert.assertThrows;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
@SpringBootTest
public class BookServiceTest {

    @Autowired
    private BookService bookService;

    @MockBean
    private BookRepository bookRepository;

    @MockBean
    private AuthorClient authorClient;

    private Book book;

    @BeforeEach
    public void setUp() {
        book = new Book(1L, "Java Programming", "John Doe", 2023, 5);
    }

    @Test
    public void testGetBookWithAuthorDetails_Success() {
        when(bookRepository.findById(1L)).thenReturn(Optional.of(book));

        AuthorDetails authorDetails = new AuthorDetails("A renowned Java developer...", "American");
        when(authorClient.getAuthorDetails("John Doe")).thenReturn(authorDetails);

        BookDto result = bookService.getBookWithAuthorDetails(1L);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("Java Programming", result.getTitle());
        assertEquals("John Doe", result.getAuthor());
        assertEquals(2023, result.getPublicationYear());
        assertEquals(5, result.getAvailableCopies());
        assertNotNull(result.getAuthorDetails());
        assertEquals("A renowned Java developer...", result.getAuthorDetails().getBiography());
    }

    @Test
    public void testGetBookWithAuthorDetails_AuthorNotFound() {
        when(bookRepository.findById(1L)).thenReturn(Optional.of(book));

        when(authorClient.getAuthorDetails("John Doe")).thenReturn(null);

        assertThrows(NotFoundException.class, () -> bookService.getBookWithAuthorDetails(1L));
    }

    @Test
    public void testGetBookWithAuthorDetails_BookNotFound() {
        when(bookRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> bookService.getBookWithAuthorDetails(1L));
    }
}
