package liaison.linkit.team.exception.log;

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
public enum TeamLogErrorCode implements BaseErrorCode {
    UPDATE_TEAM_LOG_TYPE_BAD_REQUEST(BAD_REQUEST, "TEAM_LOG_400_1", "비공개된 로그는 대표 로그로 변경할 수 없습니다."),
    UPDATE_TEAM_LOG_PUBLIC_BAD_REQUEST(
            BAD_REQUEST, "TEAM_LOG_400_2", "비공개된 로그는 대표 로그로 변경할 수 없습니다."),
    TEAM_LOG_NOT_FOUND(NOT_FOUND, "TEAM_LOG_404_1", "해당하는 팀 로그를 찾을 수 없습니다.");

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
