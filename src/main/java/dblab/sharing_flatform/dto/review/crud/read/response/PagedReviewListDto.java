package dblab.sharing_flatform.dto.review.crud.read.response;

import dblab.sharing_flatform.dto.post.PostDto;
import dblab.sharing_flatform.dto.post.crud.read.response.PagedPostListDto;
import dblab.sharing_flatform.dto.review.ReviewDto;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.Page;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PagedReviewListDto {

    private Long totalElements;
    private Integer totalPage;
    private boolean hasNextPage;
    private List<ReviewDto> reviewList;

    public static PagedReviewListDto toDto(Page<ReviewDto> page) {
        return new PagedReviewListDto(
                page.getTotalElements(),
                page.getTotalPages(),
                page.hasNext(),
                page.getContent()
        );
    }
}
