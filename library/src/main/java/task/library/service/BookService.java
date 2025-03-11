package task.library.service;

import task.library.dto.BookRequest;
import task.library.dto.BookDto;
import task.library.entity.Book;
import java.util.List;

public interface BookService {

    Book createBook(Book book);

    List<Book> getAllBooks();

    Book getBookById(Long id);

    Book updateBook(Long id, BookRequest request);

    void deleteBook(Long id);

    List<Book> searchBooks(String title, String author);

    List<Book> getBooksPublishedAfterYear(int year);

    List<Book> getBooksWithHighRatingsNativeSQL();

    List<Book> getBooksWithHighRatingsJPQL();

    BookDto getBookWithAuthorDetails(Long bookId);
}
