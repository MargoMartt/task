package task.author.controller;

import org.springframework.web.bind.annotation.PathVariable;
import task.author.dto.Author;

public interface AuthorController {
    public Author getAuthorDetails(@PathVariable String authorName);
}
