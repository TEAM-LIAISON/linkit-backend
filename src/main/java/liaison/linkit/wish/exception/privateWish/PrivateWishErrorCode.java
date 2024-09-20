package liaison.linkit.wish.exception.privateWish;

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
public enum PrivateWishErrorCode implements BaseErrorCode {

    FORBIDDEN_PRIVATE_WISH(FORBIDDEN, "PRIVATE_WISH_403_1", "내 프로필을 찜하기 할 수 없습니다."),
    PRIVATE_WISH_NOT_FOUND(NOT_FOUND, "PRIVATE_WISH_404_1", "프로필에 대한 좋아요 기록이 없습니다."),
    DUPLICATE_PRIVATE_WISH(DUPLICATE, "PRIVATE_WISH_409_1", "이미 좋아요한 프로필입니다."),
    TOO_MANY_PRIVATE_WISH_REQUEST(TOO_MANY_REQUESTS, "PRIVATE_WISH_429_1", "프로필 최대 찜하기 개수를 초과하였습니다");

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
