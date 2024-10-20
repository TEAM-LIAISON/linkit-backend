package liaison.linkit.wish.exception.privateScrap;

import static liaison.linkit.common.consts.LinkitStatic.DUPLICATE;
import static liaison.linkit.common.consts.LinkitStatic.FORBIDDEN;
import static liaison.linkit.common.consts.LinkitStatic.NOT_FOUND;
import static liaison.linkit.common.consts.LinkitStatic.TOO_MANY_REQUESTS;

import java.lang.reflect.Field;
import java.util.Objects;
import liaison.linkit.common.annotation.ExplainError;
import liaison.linkit.common.exception.BaseErrorCode;
import liaison.linkit.common.exception.ErrorReason;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum PrivateScrapErrorCode implements BaseErrorCode {
    FORBIDDEN_PRIVATE_SCRAP(FORBIDDEN, "PRIVATE_SCRAP_403_1", "내 프로필을 스크랩 할 수 없습니다."),
    PRIVATE_SCRAP_NOT_FOUND(NOT_FOUND, "PRIVATE_SCRAP_404_1", "프로필에 대한 스크랩 기록이 없습니다."),
    DUPLICATE_PRIVATE_SCRAP(DUPLICATE, "PRIVATE_SCRAP_409_1", "이미 스크랩한 프로필입니다."),
    TOO_MANY_PRIVATE_SCRAP_REQUEST(TOO_MANY_REQUESTS, "PRIVATE_SCRAP_429_1", "프로필 최대 스크랩 개수를 초과하였습니다.");

    private final Integer status;
    private final String code;
    private final String reason;

    @Override
    public ErrorReason getErrorReason() {
        return ErrorReason.builder().reason(reason).code(code).status(status).build();
    }

    @Override
    public String getExplainError() throws NoSuchFieldException {
        Field field = this.getClass().getField(this.name());
        ExplainError annotation = field.getAnnotation(ExplainError.class);
        return Objects.nonNull(annotation) ? annotation.value() : this.getReason();
    }
}
