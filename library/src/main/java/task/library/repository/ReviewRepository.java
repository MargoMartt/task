package task.library.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import task.library.dto.BookRatingResponse;
import task.library.entity.Book;
import task.library.entity.Review;
import java.util.List;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Long> {
    List<Review> findAllByBook(Book book);

    @Query("SELECT new task.library.dto.BookRatingResponse(b.title, AVG(r.rating)) " +
            "FROM Review r JOIN r.book b " +
            "GROUP BY b.id")
    List<BookRatingResponse> getAverageRatingsForBooks();

    @Query("SELECT b FROM Book b JOIN b.reviews r " +
            "GROUP BY b.id HAVING AVG(r.rating) >= 4")
    List<Book> getBooksWithHighRatingsJPQL();

}
