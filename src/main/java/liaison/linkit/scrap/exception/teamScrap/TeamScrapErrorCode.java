package liaison.linkit.scrap.exception.teamScrap;

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
public enum TeamScrapErrorCode implements BaseErrorCode {
    FORBIDDEN_TEAM_SCRAP(FORBIDDEN, "TEAM_SCRAP_403_1", "내가 속한 팀을 스크랩할 수 없습니다."),
    TEAM_SCRAP_NOT_FOUND(NOT_FOUND, "TEAM_SCRAP_404_1", "팀에 대한 스크랩 기록이 없습니다."),
    DUPLICATE_TEAM_SCRAP(DUPLICATE, "TEAM_SCRAP_409_1", "이미 스크랩한 팀입니다."),
    TOO_MANY_TEAM_SCRAP_REQUEST(TOO_MANY_REQUESTS, "TEAM_SCRAP_429_1", "팀 최대 스크랩 개수를 초과하였습니다.");

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
