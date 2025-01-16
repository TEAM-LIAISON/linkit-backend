package liaison.linkit.team.exception.teamMember;

import static liaison.linkit.common.consts.LinkitStatic.BAD_REQUEST;
import static liaison.linkit.common.consts.LinkitStatic.FORBIDDEN;

import java.lang.reflect.Field;
import java.util.Objects;
import liaison.linkit.common.annotation.ExplainError;
import liaison.linkit.common.exception.BaseErrorCode;
import liaison.linkit.common.exception.ErrorReason;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum TeamMemberErrorCode implements BaseErrorCode {
    OWNER_TEAM_MEMBER_OUT_BAD_REQUEST(BAD_REQUEST, "TEAM_MEMBER_400_1", "오너는 팀 나가기를 요청할 수 없습니다,"),
    TEAM_MEMBER_TYPE_FORBIDDEN_REQUEST(FORBIDDEN, "FORBIDDEN_TEAM_MEMBER_TYPE_REQUEST_403_1", "잘못된 팀 관리 권한 요청입니다.");

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
