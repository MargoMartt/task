package task.library.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import task.library.entity.Book;

import java.util.List;

@Repository
public interface BookRepository extends JpaRepository<Book, Long> {
    List<Book> findByTitleOrAuthor(String title, String author);

    @Query(value = "SELECT * FROM book WHERE publication_year > :year", nativeQuery = true)
    List<Book> findBooksPublishedAfterYear(@Param("year") int year);

    @Query(value = "SELECT b.* FROM book b " +
            "JOIN review r ON b.id = r.book_id " +
            "GROUP BY b.id " +
            "HAVING AVG(r.rating) >= 4", nativeQuery = true)
    List<Book> getBooksWithHighRatingsNativeSQL();

}

