package liaison.linkit.global.util;

import java.util.List;
import java.util.function.Function;

import liaison.linkit.search.presentation.dto.cursor.CursorResponse;

public class CursorUtils {
    public static <T, R> CursorResponse<R> mapCursorResponse(
            CursorResponse<T> source, Function<T, R> mapper) {
        List<R> mapped = source.getContent().stream().map(mapper).toList();
        return CursorResponse.of(mapped, source.getNextCursor());
    }
}
