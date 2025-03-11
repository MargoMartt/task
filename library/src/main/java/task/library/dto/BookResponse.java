package task.library.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class BookResponse {
    private Long id;
    private String title;
    private String author;
    private int publicationYear;
    private int availableCopies;
}
