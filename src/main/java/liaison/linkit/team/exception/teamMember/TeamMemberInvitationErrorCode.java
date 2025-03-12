package liaison.linkit.team.exception.teamMember;

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
public enum TeamMemberInvitationErrorCode implements BaseErrorCode {
    TEAM_MEMBER_INVITATION_NOT_FOUND(NOT_FOUND, "TEAM_MEMBER_404_1", "팀원 초대 정보를 찾을 수 없습니다."),
    DUPLICATE_TEAM_MEMBER_INVITATION_REQUEST(
            DUPLICATE, "DUPLICATE_INVITATION_409_1", "해당 회원에 대해 이미 진행 중인 팀 초대가 있습니다.");

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
