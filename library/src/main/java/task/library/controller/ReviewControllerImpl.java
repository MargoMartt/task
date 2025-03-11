package task.library.controller;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import task.library.dto.BookRatingResponse;
import task.library.dto.ReviewRequest;
import task.library.dto.ReviewResponse;
import task.library.entity.Review;
import task.library.mapper.ReviewMapper;
import task.library.service.ReviewService;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/reviews")
public class ReviewControllerImpl implements ReviewController {

    private final ReviewService reviewService;
    private final ReviewMapper reviewMapper;

    @PostMapping("/books/{bookId}")
    @Operation(summary = "Add a review to a book")
    public ResponseEntity<ReviewResponse> addReviewToBook(
            @PathVariable Long bookId, @RequestBody @Valid ReviewRequest reviewRequest) {
        Review review = reviewService.addReviewToBook(bookId, reviewRequest);
        ReviewResponse reviewResponse = reviewMapper.toReviewResponse(review);
        return ResponseEntity.status(HttpStatus.CREATED).body(reviewResponse);
    }

    @GetMapping("/books/{bookId}")
    @Operation(summary = "Get all reviews for a specific book")
    public ResponseEntity<List<ReviewResponse>> getAllReviewsForBook(@PathVariable Long bookId) {
        List<Review> reviews = reviewService.getAllReviewsForBook(bookId);
        List<ReviewResponse> reviewResponses = reviews.stream()
                .map(reviewMapper::toReviewResponse)
                .collect(Collectors.toList());
        return ResponseEntity.ok(reviewResponses);
    }

    @PutMapping("/{reviewId}")
    @Operation(summary = "Update an existing review")
    public ResponseEntity<ReviewResponse> updateReview(
            @PathVariable Long reviewId, @RequestBody @Valid ReviewRequest reviewRequest) {
        Review updatedReview = reviewService.updateReview(reviewId, reviewRequest);
        ReviewResponse reviewResponse = reviewMapper.toReviewResponse(updatedReview);
        return ResponseEntity.ok(reviewResponse);
    }

    @DeleteMapping("/{reviewId}")
    @Operation(summary = "Delete a review by its ID")
    public ResponseEntity<Void> deleteReview(@PathVariable Long reviewId) {
        reviewService.deleteReview(reviewId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/average-ratings")
    @Operation(summary = "Count the average rating for each book")
    public ResponseEntity<List<BookRatingResponse>> getAverageRatings() {
        List<BookRatingResponse> ratings = reviewService.getAverageRatingsForBooks();
        return ResponseEntity.ok(ratings);
    }
}

