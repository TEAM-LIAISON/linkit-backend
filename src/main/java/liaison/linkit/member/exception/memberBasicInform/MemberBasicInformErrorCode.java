package liaison.linkit.member.exception.memberBasicInform;

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
public enum MemberBasicInformErrorCode implements BaseErrorCode {
    MEMBER_BASIC_INFORM_BAD_REQUEST(BAD_REQUEST, "MEMBER_BASIC_INFORM_400_1", "회원 기본 정보 요청에 대한 오류가 발생했습니다."),
    MEMBER_BASIC_INFORM_NOT_FOUND(NOT_FOUND, "MEMBER_BASIC_INFORM_404_1", "회원 기본 정보를 찾을 수 없습니다.");

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
