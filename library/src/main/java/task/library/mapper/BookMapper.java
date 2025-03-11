package task.library.mapper;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import task.library.dto.BookRequest;
import task.library.dto.BookResponse;
import task.library.entity.Book;

@Mapper(componentModel = "spring")
public interface BookMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "reviews", ignore = true)
    Book toEntity(BookRequest request);

    BookResponse toBookResponse(Book book);

}
