package task.library.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import task.library.dto.ReviewRequest;
import task.library.dto.ReviewResponse;
import task.library.dto.BookRatingResponse;
import task.library.entity.Book;
import task.library.entity.Review;
import task.library.exception.NotFoundException;
import task.library.mapper.ReviewMapper;
import task.library.service.ReviewService;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ReviewControllerImpl.class)
@AutoConfigureMockMvc
class ReviewControllerImplTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ReviewService reviewService;

    @MockBean
    private ReviewMapper reviewMapper;

    @Autowired
    private ObjectMapper objectMapper;

    private Review review1;
    private Review review2;

    @BeforeEach
    void setUp() {
        Book book = new Book(1L, "Java Programming", "John Doe", 2023, 5);
        review1 = new Review(1L, 5, "Excellent");
        review1.setBook(book);
        review2 = new Review(2L, 4, "Great");
        review2.setBook(book);
    }

    @Test
    void testAddReviewToBook_Success() throws Exception {
        ReviewRequest reviewRequest = new ReviewRequest();
        reviewRequest.setRating(5);
        reviewRequest.setComment("Excellent");

        ReviewResponse reviewResponse = new ReviewResponse();
        reviewResponse.setId(1L);
        reviewResponse.setRating(5);
        reviewResponse.setComment("Excellent");

        when(reviewService.addReviewToBook(eq(1L), any(ReviewRequest.class))).thenReturn(review1);
        when(reviewMapper.toReviewResponse(review1)).thenReturn(reviewResponse);

        mockMvc.perform(MockMvcRequestBuilders.post("/api/reviews/books/{bookId}", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(reviewRequest))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.rating").value(5))
                .andExpect(jsonPath("$.comment").value("Excellent"));

        verify(reviewService, times(1)).addReviewToBook(eq(1L), any(ReviewRequest.class));
    }

    @Test
    void testAddReviewToBook_NotFoundBook() throws Exception {
        ReviewRequest reviewRequest = new ReviewRequest();
        reviewRequest.setRating(5);
        reviewRequest.setComment("Excellent");

        when(reviewService.addReviewToBook(eq(1L), any(ReviewRequest.class)))
                .thenThrow(NotFoundException.notFoundBook(1L));

        mockMvc.perform(MockMvcRequestBuilders.post("/api/reviews/books/{bookId}", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(reviewRequest))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Book with ID 1 not found"));

        verify(reviewService, times(1)).addReviewToBook(eq(1L), any(ReviewRequest.class));
    }

    @Test
    void testAddReviewToBook_InvalidData() throws Exception {
        ReviewRequest invalidReviewRequest = new ReviewRequest();
        invalidReviewRequest.setRating(6);
        invalidReviewRequest.setComment("Test invalid rating");

        mockMvc.perform(MockMvcRequestBuilders.post("/api/reviews/books/{bookId}", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidReviewRequest))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.rating").value("Rating must be at most 5"));
    }

    @Test
    void testGetAllReviewsForBook_Success() throws Exception {
        List<Review> reviews = List.of(review1, review2);

        ReviewResponse response1 = new ReviewResponse();
        response1.setId(1L);
        response1.setRating(5);
        response1.setComment("Excellent");

        ReviewResponse response2 = new ReviewResponse();
        response2.setId(2L);
        response2.setRating(4);
        response2.setComment("Great");

        when(reviewService.getAllReviewsForBook(1L)).thenReturn(reviews);
        when(reviewMapper.toReviewResponse(review1)).thenReturn(response1);
        when(reviewMapper.toReviewResponse(review2)).thenReturn(response2);

        mockMvc.perform(MockMvcRequestBuilders.get("/api/reviews/books/{bookId}", 1L)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].rating").value(5))
                .andExpect(jsonPath("$[0].comment").value("Excellent"))
                .andExpect(jsonPath("$[1].id").value(2L))
                .andExpect(jsonPath("$[1].rating").value(4))
                .andExpect(jsonPath("$[1].comment").value("Great"));

        verify(reviewService, times(1)).getAllReviewsForBook(1L);
    }

    @Test
    void testGetAllReviewsForBook_NotFoundReview() throws Exception {
        when(reviewService.getAllReviewsForBook(1L))
                .thenThrow(NotFoundException.notFoundReviewsForBook(1L));

        mockMvc.perform(MockMvcRequestBuilders.get("/api/reviews/books/{bookId}", 1L)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Reviews for book with ID 1 not found"));

        verify(reviewService, times(1)).getAllReviewsForBook(1L);
    }

    @Test
    void testGetAllReviewsForBook_NotFoundBook() throws Exception {
        when(reviewService.getAllReviewsForBook(1L))
                .thenThrow(NotFoundException.notFoundBook(1L));

        mockMvc.perform(MockMvcRequestBuilders.get("/api/reviews/books/{bookId}", 1L)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Book with ID 1 not found"));

        verify(reviewService, times(1)).getAllReviewsForBook(1L);
    }

    @Test
    void testUpdateReview_Success() throws Exception {
        ReviewRequest reviewRequest = new ReviewRequest();
        reviewRequest.setRating(4);
        reviewRequest.setComment("Good");

        Review updatedReview = new Review(1L, 4, "Good");
        updatedReview.setBook(review1.getBook());

        ReviewResponse reviewResponse = new ReviewResponse();
        reviewResponse.setId(1L);
        reviewResponse.setRating(4);
        reviewResponse.setComment("Good");

        when(reviewService.updateReview(eq(1L), any(ReviewRequest.class))).thenReturn(updatedReview);
        when(reviewMapper.toReviewResponse(updatedReview)).thenReturn(reviewResponse);

        mockMvc.perform(MockMvcRequestBuilders.put("/api/reviews/{reviewId}", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(reviewRequest))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.rating").value(4))
                .andExpect(jsonPath("$.comment").value("Good"));

        verify(reviewService, times(1)).updateReview(eq(1L), any(ReviewRequest.class));
    }

    @Test
    void testUpdateReview_NotFound() throws Exception {
        ReviewRequest reviewRequest = new ReviewRequest();
        reviewRequest.setRating(4);
        reviewRequest.setComment("Good");

        when(reviewService.updateReview(eq(1L), any(ReviewRequest.class)))
                .thenThrow(NotFoundException.notFoundReview(1L));

        mockMvc.perform(MockMvcRequestBuilders.put("/api/reviews/{reviewId}", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(reviewRequest))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Review with ID 1 not found"));

        verify(reviewService, times(1)).updateReview(eq(1L), any(ReviewRequest.class));
    }

    @Test
    void testUpdateReview_InvalidData() throws Exception {
        ReviewRequest invalidReviewRequest = new ReviewRequest();
        invalidReviewRequest.setRating(0);
        invalidReviewRequest.setComment("Test invalid rating");

        mockMvc.perform(MockMvcRequestBuilders.put("/api/reviews/{reviewId}", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidReviewRequest))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.rating").value("Rating must be at least 1"));
    }

    @Test
    void testDeleteReview_Success() throws Exception {
        doNothing().when(reviewService).deleteReview(1L);

        mockMvc.perform(MockMvcRequestBuilders.delete("/api/reviews/{reviewId}", 1L)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());

        verify(reviewService, times(1)).deleteReview(1L);
    }

    @Test
    void testDeleteReview_NotFound() throws Exception {
        doThrow(NotFoundException.notFoundReview(1L)).when(reviewService).deleteReview(1L);

        mockMvc.perform(MockMvcRequestBuilders.delete("/api/reviews/{reviewId}", 1L)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Review with ID 1 not found"));

        verify(reviewService, times(1)).deleteReview(1L);
    }

    @Test
    void testGetAverageRatings_Success() throws Exception {
        BookRatingResponse ratingResponse1 = new BookRatingResponse("Java Programming", 4.5);
        BookRatingResponse ratingResponse2 = new BookRatingResponse("Spring Boot Essentials", 4.0);

        List<BookRatingResponse> ratings = List.of(ratingResponse1, ratingResponse2);
        when(reviewService.getAverageRatingsForBooks()).thenReturn(ratings);

        mockMvc.perform(MockMvcRequestBuilders.get("/api/reviews/average-ratings")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].bookTitle").value("Java Programming"))
                .andExpect(jsonPath("$[0].averageRating").value(4.5))
                .andExpect(jsonPath("$[1].bookTitle").value("Spring Boot Essentials"))
                .andExpect(jsonPath("$[1].averageRating").value(4.0));

        verify(reviewService, times(1)).getAverageRatingsForBooks();
    }

    @Test
    void testGetAverageRatings_NotFound() throws Exception {
        when(reviewService.getAverageRatingsForBooks()).thenThrow(NotFoundException.notFoundAnyRatingForAnyBook());

        mockMvc.perform(MockMvcRequestBuilders.get("/api/reviews/average-ratings")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("No book has a rating"));

        verify(reviewService, times(1)).getAverageRatingsForBooks();
    }
}
