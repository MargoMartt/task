package task.library.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import task.library.config.feign.AuthorClient;
import task.library.dto.AuthorDetails;
import task.library.dto.BookRequest;
import task.library.dto.BookDto;
import task.library.entity.Book;
import task.library.exception.NotFoundException;
import task.library.repository.BookRepository;
import task.library.repository.ReviewRepository;
import java.util.List;

@Service
@RequiredArgsConstructor
public class BookServiceImpl implements BookService {

    private final BookRepository bookRepository;
    private final ReviewRepository reviewRepository;
    private final AuthorClient authorClient;

    public Book createBook(Book book) {
        return bookRepository.save(book);
    }

    public List<Book> getAllBooks() {
        List<Book> books = bookRepository.findAll();
        if (books.isEmpty()) {
            throw NotFoundException.notFoundBooks();
        }
        return books;
    }

    public Book getBookById(Long id) {
        return bookRepository.findById(id)
                .orElseThrow(() -> NotFoundException.notFoundBook(id));
    }

    public Book updateBook(Long id, BookRequest request) {
        Book book = bookRepository.findById(id)
                .orElseThrow(() -> NotFoundException.notFoundBook(id));
        book.setTitle(request.getTitle());
        book.setAuthor(request.getAuthor());
        book.setPublicationYear(request.getPublicationYear());
        book.setAvailableCopies(request.getAvailableCopies());
        return bookRepository.save(book);
    }

    public void deleteBook(Long id) {
        if (!bookRepository.existsById(id)) {
            throw NotFoundException.notFoundBook(id);
        }
        bookRepository.deleteById(id);
    }

    public List<Book> searchBooks(String title, String author) {
        List<Book> books = bookRepository.findByTitleOrAuthor(title, author);
        if (books.isEmpty()) {
            throw NotFoundException.notFoundBooksByTitleOrAuthor(title, author);
        }
        return books;
    }

    public List<Book> getBooksPublishedAfterYear(int year) {
        List<Book> books = bookRepository.findBooksPublishedAfterYear(year);
        if (books.isEmpty()) {
            throw NotFoundException.notFoundBooksAfterYear(year);
        }
        return books;
    }

    public List<Book> getBooksWithHighRatingsNativeSQL() {
        List<Book> books = bookRepository.getBooksWithHighRatingsNativeSQL();
        if (books.isEmpty()) {
            throw NotFoundException.notFoundBookWithHighRating();
        }
        return books;
    }

    public List<Book> getBooksWithHighRatingsJPQL() {
        List<Book> books = reviewRepository.getBooksWithHighRatingsJPQL();
        if (books.isEmpty()) {
            throw NotFoundException.notFoundBookWithHighRating();
        }
        return books;
    }

    public BookDto getBookWithAuthorDetails(Long bookId) {
        Book book = getBookById(bookId);
        BookDto bookDto = new BookDto();
        bookDto.setId(bookId);
        bookDto.setTitle(book.getTitle());
        bookDto.setAuthor(book.getAuthor());
        bookDto.setPublicationYear(book.getPublicationYear());
        bookDto.setAvailableCopies(book.getAvailableCopies());

        AuthorDetails authorDetails = authorClient.getAuthorDetails(book.getAuthor());
        if (authorDetails==null) {
            throw NotFoundException.authorNotFoundException();
        }
        bookDto.setAuthorDetails(authorDetails);

        return bookDto;
    }

}
