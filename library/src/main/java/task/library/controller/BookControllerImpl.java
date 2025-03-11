package task.library.controller;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import task.library.dto.BookRequest;
import task.library.dto.BookResponse;
import task.library.dto.BookDto;
import task.library.entity.Book;
import task.library.mapper.BookMapper;
import task.library.service.BookService;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/books")
public class BookControllerImpl implements BookController {

    private final BookService bookService;
    private final BookMapper bookMapper;

    @PostMapping
    @Operation(summary = "Create new book")
    public ResponseEntity<Book> createBook(@RequestBody @Valid BookRequest book) {
        Book createdBook = bookService.createBook(bookMapper.toEntity(book));
        return ResponseEntity.status(HttpStatus.CREATED).body(createdBook);
    }

    @GetMapping
    @Operation(summary = "Retrieve all books (without reviews)")
    public ResponseEntity<List<BookResponse>> getAllBooks() {
        List<Book> books = bookService.getAllBooks();
        List<BookResponse> bookResponses = books.stream()
                .map(bookMapper::toBookResponse)
                .collect(Collectors.toList());
        return ResponseEntity.ok(bookResponses);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get a book by its ID")
    public ResponseEntity<Book> getBookById(@PathVariable Long id) {
        Book book = bookService.getBookById(id);
        return ResponseEntity.ok(book);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update an existing book")
    public ResponseEntity<Book> updateBook(@PathVariable Long id, @RequestBody @Valid BookRequest bookUpdateRequest) {
        Book updatedBook = bookService.updateBook(id, bookUpdateRequest);
        return ResponseEntity.ok(updatedBook);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Delete a book by its ID")
    public void deleteBook(@PathVariable Long id) {
        bookService.deleteBook(id);
    }

    @GetMapping("/search")
    @Operation(summary = "Search books by title or author")
    public ResponseEntity<List<Book>> searchBooks(@RequestParam(required = false) String title, @RequestParam(required = false) String author) {
        List<Book> books = bookService.searchBooks(title, author);
        return ResponseEntity.ok(books);
    }

    @GetMapping("/published-after")
    @Operation(summary = "Get books published after a specific year")
    public ResponseEntity<List<Book>> getBooksPublishedAfterYear(
            @RequestParam int year) {
        List<Book> books = bookService.getBooksPublishedAfterYear(year);
        return ResponseEntity.ok(books);
    }

    @GetMapping("/high-ratings-sql")
    @Operation(summary = "Get all books with rating hire than four (Using SQL)")
    public ResponseEntity<List<Book>> getBooksWithHighRatingsSQL() {
        List<Book> books = bookService.getBooksWithHighRatingsNativeSQL();
        return ResponseEntity.ok(books);
    }

    @GetMapping("/high-ratings-jpql")
    @Operation(summary = "Get all books with rating hire than four (Using JPQL)")
    public ResponseEntity<List<Book>> getBooksWithHighRatingsJPQL() {
        List<Book> books = bookService.getBooksWithHighRatingsJPQL();
        return ResponseEntity.ok(books);
    }

    @GetMapping("/{id}/author-details")
    @Operation(summary = "Get book and author details from external api")
    public BookDto getBook(@PathVariable Long id) {
        return bookService.getBookWithAuthorDetails(id);
    }
}

