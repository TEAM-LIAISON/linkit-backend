package liaison.linkit.member.exception.member;

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
public enum MemberErrorCode implements BaseErrorCode {
    FAIL_MEMBER_GENERATE(410, "MEMBER_410_1", "회원 생성 과정에서 문제가 발생했습니다."),
    MEMBER_NOT_FOUND(NOT_FOUND, "MEMBER_404_1", "회원을 찾을 수 없습니다."),
    DUPLICATE_EMAIL_ID(DUPLICATE, "MEMBER_409_1", "현재 사용 중인 이메일 ID가 있습니다.");

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
