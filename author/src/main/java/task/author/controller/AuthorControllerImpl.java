package task.author.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import task.author.dto.Author;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/authors")
public class AuthorControllerImpl {


    private static final Map<String, Author> authors = new HashMap<>();

    static {
        authors.put("John Doe", new Author("John Doe", "A renowned Java developer...", "American"));
        authors.put("Jane Smith", new Author("Jane Smith", "A prolific tech writer...", "British"));
    }

    @GetMapping("/{authorName}")
    public Author getAuthorDetails(@PathVariable String authorName) {
        return authors.getOrDefault(authorName, new Author(authorName, "Biography not found", "Unknown"));
    }
}