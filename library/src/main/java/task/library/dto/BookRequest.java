package task.library.dto;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class BookRequest {

    @NotBlank(message = "Title is required")
    private String title;

    @NotBlank(message = "Author is required")
    private String author;

    @NotNull(message = "Publication year is required")
    @Min(value = 0, message = "Publication year must be a positive number")
    @Max(value = 2025, message = "Publication year must be at most 2025")
    private Integer publicationYear;

    @NotNull(message = "Available copies is required")
    @Min(value = 0, message = "Available copies must be a positive number")
    private Integer availableCopies;
}
