package dblab.sharing_platform.dto.message;

import lombok.AllArgsConstructor;
import lombok.Data;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

@Data
@AllArgsConstructor
public class MessagePagingCondition {

    @NotNull(message = "페이지 번호를 입력해주세요.")
    @PositiveOrZero(message = "0 이상의 올바른 페이지 번호를 입력해주세요.")
    private Integer page;

    @NotNull(message = "페이지 크기를 입력해주세요.")
    @Positive(message = "1 이상의 올바른 페이지당 글 개수를 입력해주세요.")
    private Integer size;

    private String senderUsername;
    private String receiverUsername;
    private String senderName;
    private String receiverName;

    public MessagePagingCondition() {
        this.page = getDefaultPageNum();
        this.size = getDefaultPageSize();
    }

    private int getDefaultPageNum() {
        return 0;
    }

    private int getDefaultPageSize() {
        return 10;
    }
}
