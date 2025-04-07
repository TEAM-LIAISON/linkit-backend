package liaison.linkit.search.presentation.dto.cursor;

import org.springframework.util.StringUtils;

public record CursorRequest(String cursor, int size) {

    public boolean hasNext() {
        return StringUtils.hasText(cursor);
    }

    public CursorRequest next(String nextCursor) {
        return new CursorRequest(nextCursor, size);
    }
}
