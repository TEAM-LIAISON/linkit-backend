package liaison.linkit.login.exception;

import static liaison.linkit.common.consts.LinkitStatic.BAD_REQUEST;
import static liaison.linkit.common.consts.LinkitStatic.DUPLICATE;

import java.lang.reflect.Field;
import java.util.Objects;
import liaison.linkit.common.annotation.ExplainError;
import liaison.linkit.common.exception.BaseErrorCode;
import liaison.linkit.common.exception.ErrorReason;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum LoginErrorCode implements BaseErrorCode {
    AUTH_CODE_BAD_REQUEST(BAD_REQUEST, "AUTH_CODE_400_1", "잘못된 인증 코드 요청입니다."),
    QUIT_BAD_REQUEST(BAD_REQUEST, "QUIT_400_1", "다른 관리자가 팀에 등록된 상태에서 회원탈퇴를 할 수 없습니다."),
    DUPLICATE_EMAIL_REQUEST(DUPLICATE, "DUPLICATE_EMAIL_409_1", "중복된 이메일에 대한 로그인 요청입니다.");

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
