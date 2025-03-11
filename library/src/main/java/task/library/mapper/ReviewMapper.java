package task.library.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import task.library.dto.ReviewRequest;
import task.library.dto.ReviewResponse;
import task.library.entity.Review;

@Mapper(componentModel = "spring")
public interface ReviewMapper {

    @Mapping(source = "id", target = "id")
    @Mapping(source = "rating", target = "rating")
    @Mapping(source = "comment", target = "comment")
    ReviewResponse toReviewResponse(Review review);

    Review toReview(ReviewRequest reviewRequest);
}

