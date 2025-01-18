package liaison.linkit.team.exception.team;

import static liaison.linkit.common.consts.LinkitStatic.DUPLICATE;
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
public enum TeamErrorCode implements BaseErrorCode {
    DELETE_TEAM_BAD_REQUEST(NOT_FOUND, "TEAM_400_1", "해당 팀의 오너 또는 관리자만 팀을 삭제할 수 있습니다."),
    TEAM_NOT_FOUND(NOT_FOUND, "TEAM_404_1", "해당하는 TeamCode를 가진 팀을 찾을 수 없습니다."),
    DUPLICATE_TEAM_CODE(DUPLICATE, "TEAM_409_1", "이미 존재하는 팀 아이디입니다.");

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
