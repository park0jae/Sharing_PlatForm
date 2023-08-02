package dblab.sharing_flatform.dto.trade;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.boot.convert.DataSizeUnit;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

@Data
@AllArgsConstructor
public class TradePagingCondition {
    @NotNull(message = "페이지 번호를 입력해주세요.")
    @PositiveOrZero(message = "0 이상의 올바른 페이지 번호를 입력해주세요.")
    private Integer page;

    @NotNull(message = "페이지 크기를 입력해주세요.")
    @Positive(message = "1 이상의 올바른 페이지당 글 개수를 입력해주세요.")
    private Integer size;

    // 검색 조건
    private Long postId; // 게시글 ID
    private String renderMember;
    private String borrowerMember;
    private boolean tradeComplete; // 거래 완료 여부
    private String categoryName; // 카테고리 이름 

    public TradePagingCondition() {
        this.page = getDefaultPageNum();
        this.size = getDefaultPageSize();
    }

    private int getDefaultPageNum() {
        // 디폴트 페이지 번호 로직
        return 0; // 예시로 1로 설정
    }

    private int getDefaultPageSize() {
        // 디폴트 페이지당 글 개수 로직
        return 10; // 예시로 10으로 설정
    }
}
