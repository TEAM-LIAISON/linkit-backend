package liaison.linkit.file.exception.file;

import static liaison.linkit.common.consts.LinkitStatic.BAD_REQUEST;
import static liaison.linkit.common.consts.LinkitStatic.INTERNAL_SERVER;
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
public enum FileErrorCode implements BaseErrorCode {
    EMPTY_FILE_REQUEST(BAD_REQUEST, "FILE_400_1", "요청한 파일이 없습니다."),
    INVALID_FILE_REQUEST(BAD_REQUEST, "FILE_400_2", "유효하지 않은 파일입니다."),
    INVALID_FILE_PATH(NOT_FOUND, "FILE_404_1", "유효하지 않은 파일 경로입니다."),
    FILE_NAME_HASH_ERROR(INTERNAL_SERVER, "FILE_500_1", "유효하지 않은 파일 경로입니다.");

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
