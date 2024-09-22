package liaison.linkit.wish.exception.teamWish;

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
public enum TeamWishErrorCode implements BaseErrorCode {
    FORBIDDEN_TEAM_WISH(FORBIDDEN, "TEAM_WISH_403_1", "내가 올린 팀원 공고를 찜할 수 없습니다."),
    TEAM_WISH_NOT_FOUND(NOT_FOUND, "TEAM_WISH_404_1", "팀원 공고에 대한 좋아요 기록이 없습니다."),
    DUPLICATE_TEAM_WISH(DUPLICATE, "TEAM_WISH_409_1", "이미 좋아요한 팀원 공고입니다."),
    TOO_MANY_TEAM_WISH_REQUEST(TOO_MANY_REQUESTS, "TEAM_WISH_429_1", "팀원 공고 최대 찜하기 개수를 초과하였습니다.");

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
