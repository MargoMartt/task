package task.library.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class BookRatingResponse {
    private String bookTitle;
    private Double averageRating;
}
