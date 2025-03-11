package task.library.controller;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import task.library.dto.BookDto;
import task.library.dto.BookRequest;
import task.library.dto.BookResponse;
import task.library.entity.Book;

import java.util.List;

public interface BookController {

    @Operation(summary = "Create new book")
    ResponseEntity<Book> createBook(@RequestBody @Valid BookRequest book);

    @Operation(summary = "Retrieve all books (without reviews)")
    ResponseEntity<List<BookResponse>> getAllBooks();

    @Operation(summary = "Get a book by its ID")
    ResponseEntity<Book> getBookById(@PathVariable Long id);

    @Operation(summary = "Update an existing book")
    ResponseEntity<Book> updateBook(@PathVariable Long id, @RequestBody @Valid BookRequest bookUpdateRequest);

    @Operation(summary = "Delete a book by its ID")
    void deleteBook(@PathVariable Long id);

    @Operation(summary = "Search books by title or author")
    ResponseEntity<List<Book>> searchBooks(@RequestParam(required = false) String title, @RequestParam(required = false) String author);

    @Operation(summary = "Get books published after a specific year")
    public ResponseEntity<List<Book>> getBooksPublishedAfterYear(@RequestParam int year);

    @Operation(summary = "Get all books with rating hire than four (Using SQL)")
    public ResponseEntity<List<Book>> getBooksWithHighRatingsSQL();

    @Operation(summary = "Get all books with rating hire than four (Using JPQL)")
    public ResponseEntity<List<Book>> getBooksWithHighRatingsJPQL();

    @Operation(summary = "Get book and author details from external api")
    public BookDto getBook(@PathVariable Long id);
}
