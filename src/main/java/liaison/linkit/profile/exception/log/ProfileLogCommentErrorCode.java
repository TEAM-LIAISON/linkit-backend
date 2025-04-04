package liaison.linkit.profile.exception.log;

import static liaison.linkit.common.consts.LinkitStatic.BAD_REQUEST;
import static liaison.linkit.common.consts.LinkitStatic.NOT_FOUND;

import java.lang.reflect.Field;
import java.util.Objects;

import liaison.linkit.common.annotation.ExplainError;
import liaison.linkit.common.exception.BaseErrorCode;
import liaison.linkit.common.exception.ErrorReason;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ProfileLogCommentErrorCode implements BaseErrorCode {
    PRIVATE_PROFILE_LOG_COMMENT_BAD_REQUEST(
            BAD_REQUEST, "PROFILE_LOG_COMMENT_400_1", "비공개된 로그는 로그 작성자만 댓글을 작성할 수 있습니다."),
    PARENT_COMMENT_BAD_REQUEST(BAD_REQUEST, "PROFILE_LOG_COMMENT_400_2", "답글에는 댓글을 달 수 없습니다."),

    PROFILE_LOG_COMMENT_NOT_FOUND(NOT_FOUND, "PROFILE_LOG_COMMENT_404_1", "찾으려는 프로필 로그 댓글이 없습니다.");

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
