package task.library.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import task.library.dto.AuthorDetails;
import task.library.dto.BookDto;
import task.library.dto.BookRequest;
import task.library.dto.BookResponse;
import task.library.entity.Book;
import task.library.entity.Review;
import task.library.exception.NotFoundException;
import task.library.mapper.BookMapper;
import task.library.service.BookService;
import java.util.Arrays;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(BookControllerImpl.class)
@AutoConfigureMockMvc
class BookControllerImplTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private BookService bookService;

    @MockBean
    private BookMapper bookMapper;

    private Book book;

    @BeforeEach
    void setUp() {
        book = new Book(1L, "Java Programming", "John Doe", 2023, 5);
    }

    @Test
    void testCreateBook_Success() throws Exception {
        when(bookMapper.toEntity(any())).thenReturn(book);
        when(bookService.createBook(any())).thenReturn(book);

        mockMvc.perform(MockMvcRequestBuilders.post("/api/books")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"title\": \"Java Programming\", \"author\": \"John Doe\", \"publicationYear\": 2023, \"availableCopies\": 5}")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.title").value("Java Programming"))
                .andExpect(jsonPath("$.author").value("John Doe"))
                .andExpect(jsonPath("$.publicationYear").value(2023))
                .andExpect(jsonPath("$.availableCopies").value(5));

        verify(bookService, times(1)).createBook(any());
    }

    @Test
    void testCreateBook_InvalidData() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post("/api/books")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"title\": \"\", \"author\": \"\", \"publicationYear\": 3000, \"availableCopies\": -1}")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.title").value("Title is required"))
                .andExpect(jsonPath("$.author").value("Author is required"))
                .andExpect(jsonPath("$.publicationYear").value("Publication year must be at most 2025"))
                .andExpect(jsonPath("$.availableCopies").value("Available copies must be a positive number"));
    }


    @Test
    void testGetAllBooks_Success() throws Exception {
        List<Book> books = Arrays.asList(
                new Book(1L, "Java", "Author1", 2023, 5),
                new Book(2L, "Spring Boot", "Author2", 2022, 3)
        );
        List<BookResponse> bookResponses = Arrays.asList(
                new BookResponse(1L, "Java", "Author1", 2023, 5),
                new BookResponse(2L, "Spring Boot", "Author2", 2022, 3)
        );

        when(bookService.getAllBooks()).thenReturn(books);
        when(bookMapper.toBookResponse(any())).thenReturn(bookResponses.get(0), bookResponses.get(1));

        mockMvc.perform(MockMvcRequestBuilders.get("/api/books")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(2))
                .andExpect(jsonPath("$[0].title").value("Java"))
                .andExpect(jsonPath("$[1].title").value("Spring Boot"));

        verify(bookService, times(1)).getAllBooks();
    }

    @Test
    void testGetAllBooks_NoBooksFound() throws Exception {
        when(bookService.getAllBooks()).thenThrow(NotFoundException.notFoundBooks());

        mockMvc.perform(MockMvcRequestBuilders.get("/api/books")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Books not found"));

        verify(bookService, times(1)).getAllBooks();
    }



    @Test
    void testGetBookById_Success() throws Exception {
        when(bookService.getBookById(1L)).thenReturn(book);

        mockMvc.perform(MockMvcRequestBuilders.get("/api/books/1")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.title").value("Java Programming"))
                .andExpect(jsonPath("$.author").value("John Doe"))
                .andExpect(jsonPath("$.publicationYear").value(2023))
                .andExpect(jsonPath("$.availableCopies").value(5));

        verify(bookService, times(1)).getBookById(1L);
    }

    @Test
    void testGetBookById_NotFound() throws Exception {
        when(bookService.getBookById(99L)).thenThrow( NotFoundException.notFoundBook(99L));

        mockMvc.perform(MockMvcRequestBuilders.get("/api/books/99")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Book with ID 99 not found"));

        verify(bookService, times(1)).getBookById(99L);
    }


    @Test
    void testUpdateBook_Success() throws Exception {
        Book updatedBook = new Book(1L, "Updated Java", "Jane Doe", 2024, 4);

        when(bookMapper.toEntity(any())).thenReturn(updatedBook);
        when(bookService.updateBook(eq(1L), any(BookRequest.class))).thenReturn(updatedBook);

        mockMvc.perform(MockMvcRequestBuilders.put("/api/books/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"title\": \"Updated Java\", \"author\": \"Jane Doe\", \"publicationYear\": 2024, \"availableCopies\": 4}")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.title").value("Updated Java"))
                .andExpect(jsonPath("$.author").value("Jane Doe"))
                .andExpect(jsonPath("$.publicationYear").value(2024))
                .andExpect(jsonPath("$.availableCopies").value(4));

        verify(bookService, times(1)).updateBook(eq(1L), any(BookRequest.class));
    }
    @Test
    void testUpdateBook_NotFound() throws Exception {
        when(bookService.updateBook(eq(1L), any(BookRequest.class)))
                .thenThrow(NotFoundException.notFoundBook(1L));

        mockMvc.perform(MockMvcRequestBuilders.put("/api/books/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"title\": \"Updated Java\", \"author\": \"Jane Doe\", \"publicationYear\": 2024, \"availableCopies\": 4}")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Book with ID 1 not found"));

        verify(bookService, times(1)).updateBook(eq(1L), any(BookRequest.class));
    }

    @Test
    void testUpdateBook_InvalidData() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.put("/api/books/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"title\": \"\", \"author\": \"\", \"publicationYear\": 3000, \"availableCopies\": -1}")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }


    @Test
    void testDeleteBook_Success() throws Exception {
        doNothing().when(bookService).deleteBook(1L);

        mockMvc.perform(MockMvcRequestBuilders.delete("/api/books/1")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());

        verify(bookService, times(1)).deleteBook(1L);
    }

    @Test
    void testDeleteBook_NotFound() throws Exception {
        doThrow( NotFoundException.notFoundBook(99L)).when(bookService).deleteBook(99L);

        mockMvc.perform(MockMvcRequestBuilders.delete("/api/books/99")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Book with ID 99 not found"));

        verify(bookService, times(1)).deleteBook(99L);
    }

    @Test
    void testSearchBooks_Success() throws Exception {
        List<Book> books = List.of(
                new Book(1L, "Java Programming", "John Doe", 2023, 5),
                new Book(2L, "Spring Boot Essentials", "Jane Smith", 2022, 4)
        );

        when(bookService.searchBooks("Java Programming", "John Doe")).thenReturn(books);

        mockMvc.perform(MockMvcRequestBuilders.get("/api/books/search")
                        .param("title", "Java Programming")
                        .param("author", "John Doe")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].title").value("Java Programming"))
                .andExpect(jsonPath("$[0].author").value("John Doe"))
                .andExpect(jsonPath("$[1].id").value(2L))
                .andExpect(jsonPath("$[1].title").value("Spring Boot Essentials"))
                .andExpect(jsonPath("$[1].author").value("Jane Smith"));

        verify(bookService, times(1)).searchBooks("Java Programming", "John Doe");
    }

    @Test
    void testSearchBooks_NotFound() throws Exception {
        String title = "NonExistingTitle";
        String author = "NonExistingAuthor";
        when(bookService.searchBooks(title, author))
                .thenThrow(NotFoundException.notFoundBooksByTitleOrAuthor(title, author));

        mockMvc.perform(MockMvcRequestBuilders.get("/api/books/search")
                        .param("title", title)
                        .param("author", author)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("No books found for the given title 'NonExistingTitle' or author 'NonExistingAuthor'"));

        verify(bookService, times(1)).searchBooks(title, author);
    }

    @Test
    void testGetBooksPublishedAfterYear_success() throws Exception {
        List<Book> books = List.of(
                new Book(1L, "Java Programming", "John Doe", 2023, 5),
                new Book(2L, "Spring Boot Essentials", "Jane Smith", 2024, 4)
        );

        when(bookService.getBooksPublishedAfterYear(2022)).thenReturn(books);

        mockMvc.perform(MockMvcRequestBuilders.get("/api/books/published-after")
                        .param("year", "2022")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].title").value("Java Programming"))
                .andExpect(jsonPath("$[0].author").value("John Doe"))
                .andExpect(jsonPath("$[1].id").value(2L))
                .andExpect(jsonPath("$[1].title").value("Spring Boot Essentials"))
                .andExpect(jsonPath("$[1].author").value("Jane Smith"));

        verify(bookService, times(1)).getBooksPublishedAfterYear(2022);
    }
    @Test
    void testGetBooksPublishedAfterYear_NotFound() throws Exception {
        when(bookService.getBooksPublishedAfterYear(2030))
                .thenThrow(NotFoundException.notFoundBooksAfterYear(2030));

        mockMvc.perform(MockMvcRequestBuilders.get("/api/books/published-after")
                        .param("year", "2030")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Book after 2030 year wasn't published"));

        verify(bookService, times(1)).getBooksPublishedAfterYear(2030);
    }

    @Test
    void testGetBooksWithHighRatingsSQL_Success() throws Exception {
        Book book1 = new Book(1L, "Java Programming", "John Doe", 2023, 5);
        Review review1 = new Review(1L, 5, "Excellent");
        review1.setBook(book1);
        book1.setReviews(List.of(review1));

        Book book2 = new Book(2L, "Spring Boot", "Jane Doe", 2022, 3);
        Review review2 = new Review(2L, 4, "Great");
        review2.setBook(book2);
        book2.setReviews(List.of(review2));

        List<Book> books = List.of(book1, book2);

        when(bookService.getBooksWithHighRatingsNativeSQL()).thenReturn(books);

        mockMvc.perform(MockMvcRequestBuilders.get("/api/books/high-ratings-sql")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].title").value("Java Programming"))
                .andExpect(jsonPath("$[0].author").value("John Doe"))
                .andExpect(jsonPath("$[0].publicationYear").value(2023))
                .andExpect(jsonPath("$[0].availableCopies").value(5))
                .andExpect(jsonPath("$[0].reviews[0].id").value(1L))
                .andExpect(jsonPath("$[0].reviews[0].rating").value(5))
                .andExpect(jsonPath("$[0].reviews[0].comment").value("Excellent"));

        verify(bookService, times(1)).getBooksWithHighRatingsNativeSQL();
    }
    @Test
    void testGetBooksWithHighRatingsSQL_NotFound() throws Exception {
        when(bookService.getBooksWithHighRatingsNativeSQL())
                .thenThrow(NotFoundException.notFoundBookWithHighRating());

        mockMvc.perform(MockMvcRequestBuilders.get("/api/books/high-ratings-sql")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("No book has a rating hire than Four"));

        verify(bookService, times(1)).getBooksWithHighRatingsNativeSQL();
    }

    @Test
    void testGetBooksWithHighRatingsJPQL_Success() throws Exception {
        Book book1 = new Book(1L, "Java Programming", "John Doe", 2023, 5);
        Review review1 = new Review(1L, 5, "Excellent");
        review1.setBook(book1);
        book1.setReviews(List.of(review1));

        Book book2 = new Book(2L, "Spring Boot", "Jane Doe", 2022, 3);
        Review review2 = new Review(2L, 4, "Great");
        review2.setBook(book2);
        book2.setReviews(List.of(review2));

        List<Book> books = List.of(book1, book2);

        when(bookService.getBooksWithHighRatingsJPQL()).thenReturn(books);

        mockMvc.perform(MockMvcRequestBuilders.get("/api/books/high-ratings-jpql")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].title").value("Java Programming"))
                .andExpect(jsonPath("$[0].author").value("John Doe"))
                .andExpect(jsonPath("$[0].publicationYear").value(2023))
                .andExpect(jsonPath("$[0].availableCopies").value(5))
                .andExpect(jsonPath("$[0].reviews[0].id").value(1L))
                .andExpect(jsonPath("$[0].reviews[0].rating").value(5))
                .andExpect(jsonPath("$[0].reviews[0].comment").value("Excellent"));

        verify(bookService, times(1)).getBooksWithHighRatingsJPQL();
    }

    @Test
    void testGetBooksWithHighRatingsJPQL_NotFound() throws Exception {
        when(bookService.getBooksWithHighRatingsJPQL())
                .thenThrow(NotFoundException.notFoundBookWithHighRating());

        mockMvc.perform(MockMvcRequestBuilders.get("/api/books/high-ratings-jpql")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("No book has a rating hire than Four"));

        verify(bookService, times(1)).getBooksWithHighRatingsJPQL();
    }

    @Test
    void testGetBookWithAuthorDetails_Success() throws Exception {
        BookDto bookDto = new BookDto(1L, "Java", "John Doe", 2023, 5,
                new AuthorDetails("Java expert", "American"));

        when(bookService.getBookWithAuthorDetails(1L)).thenReturn(bookDto);

        mockMvc.perform(MockMvcRequestBuilders.get("/api/books/1/author-details")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.author").value("John Doe"))
                .andExpect(jsonPath("$.authorDetails.biography").value("Java expert"));

        verify(bookService, times(1)).getBookWithAuthorDetails(1L);
    }
    @Test
    void testGetBookWithAuthorDetails_NotFound() throws Exception {
        when(bookService.getBookWithAuthorDetails(1L)).thenThrow(NotFoundException.authorNotFoundException());

        mockMvc.perform(MockMvcRequestBuilders.get("/api/books/1/author-details")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Author not found"));

        verify(bookService, times(1)).getBookWithAuthorDetails(1L);
    }
}
