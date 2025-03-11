package task.library.controller;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import task.library.dto.BookRatingResponse;
import task.library.dto.ReviewRequest;
import task.library.dto.ReviewResponse;

import java.util.List;

public interface ReviewController {

    @Operation(summary = "Add a review to a book")
    ResponseEntity<ReviewResponse> addReviewToBook(
            @PathVariable Long bookId, @RequestBody @Valid ReviewRequest reviewRequest);

    @Operation(summary = "Get all reviews for a specific book")
    ResponseEntity<List<ReviewResponse>> getAllReviewsForBook(@PathVariable Long bookId);

    @Operation(summary = "Update an existing review")
    ResponseEntity<ReviewResponse> updateReview(
            @PathVariable Long reviewId, @RequestBody @Valid ReviewRequest reviewRequest);

    @Operation(summary = "Delete a review by its ID")
    ResponseEntity<Void> deleteReview(@PathVariable Long reviewId);

    @Operation(summary = "Count the average rating for each book")
    public ResponseEntity<List<BookRatingResponse>> getAverageRatings();
}
