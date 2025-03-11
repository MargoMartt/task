package task.library.exception;

public class NotFoundException extends RuntimeException {
    public NotFoundException(String message) {
        super(message);
    }

    public static NotFoundException notFoundBook(Long bookId) {
        String message = String.format("Book with ID %d not found", bookId);
        return new NotFoundException(message);
    }

    public static NotFoundException notFoundBooks() {
        String message = "Books not found";
        return new NotFoundException(message);
    }

    public static NotFoundException notFoundReview(Long reviewId) {
        String message = String.format("Review with ID %d not found", reviewId);
        return new NotFoundException(message);
    }

    public static NotFoundException notFoundReviewsForBook(Long bookId) {
        String message = String.format("Reviews for book with ID %d not found", bookId);
        return new NotFoundException(message);
    }

    public static NotFoundException notFoundBooksByTitleOrAuthor(String title, String author) {
        String titleMessage = (title != null) ? title : "Unknown title";
        String authorMessage = (author != null) ? author : "Unknown author";

        String message = String.format("No books found for the given title '%s' or author '%s'", titleMessage, authorMessage);
        return new NotFoundException(message);
    }

    public static NotFoundException notFoundBooksAfterYear(int year) {
        String message = String.format("Book after %d year wasn't published", year);
        return new NotFoundException(message);
    }

    public static NotFoundException notFoundAnyRatingForAnyBook() {
        String message = "No book has a rating";
        return new NotFoundException(message);
    }

    public static NotFoundException notFoundBookWithHighRating() {
        String message = "No book has a rating hire than Four";
        return new NotFoundException(message);
    }

    public static NotFoundException authorNotFoundException() {
        String message = "Author not found";
        return new NotFoundException(message);
    }

}

