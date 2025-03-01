package liaison.linkit.search.presentation.dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/** 커서 기반 페이지네이션 응답 DTO */
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CursorResponse<T> {
    // 조회된 데이터 목록
    private List<T> content;

    // 다음 커서 값
    private Long nextCursor;

    // 다음 페이지 존재 여부
    private boolean hasNext;

    /**
     * 페이지네이션된 데이터와 다음 커서를 설정하여 응답 객체를 생성합니다.
     *
     * @param content 페이지네이션된 데이터 목록
     * @param nextCursor 다음 커서 값
     */
    public static <T> CursorResponse<T> of(List<T> content, Long nextCursor) {
        return CursorResponse.<T>builder()
                .content(content)
                .nextCursor(nextCursor)
                .hasNext(nextCursor != null)
                .build();
    }
}
