package liaison.linkit.search.presentation.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 커서 기반 페이지네이션 요청 DTO
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class CursorRequest {
    // 현재 커서 (마지막으로 조회한 ID)
    private String cursor;

    // 페이지 크기
    private int size;

    public boolean hasNext() {
        return cursor != null;
    }

    /**
     * 다음 커서 값과 함께 새로운 CursorRequest 객체를 생성합니다.
     */
    public CursorRequest next(String nextCursor) {
        return new CursorRequest(nextCursor, size);
    }
}
