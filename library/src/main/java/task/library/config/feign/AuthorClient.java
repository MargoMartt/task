package task.library.config.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import task.library.dto.AuthorDetails;

@FeignClient(name = "author-service", url = "http://localhost:8080/api/authors", configuration = FeignConfiguration.class)
public interface AuthorClient {

    @GetMapping("/{authorName}")
    AuthorDetails getAuthorDetails(@PathVariable("authorName") String authorName);
}
