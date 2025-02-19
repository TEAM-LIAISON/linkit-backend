package liaison.linkit.matching.exception;

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
public enum MatchingErrorCode implements BaseErrorCode {
    MATCHING_RELATION_BAD_REQUEST(BAD_REQUEST, "MATCHING_400_1", "팀으로 팀원 공고에 매칭 요청을 보낼 수 없습니다."),
    RECEIVED_MATCHING_READ_BAD_REQUEST(BAD_REQUEST, "MATCHING_400_2", "올바르지 않은 읽음처리 요청입니다."),
    COMPLETED_MATCHING_READ_BAD_REQUEST(BAD_REQUEST, "MATCHING_400_3", "올바르지 않은 읽음처리 요청입니다."),
    
    MATCHING_SENDER_BAD_REQUEST(BAD_REQUEST, "MATCHING_400_4", "올바르지 않은 발신자 정보를 입력하였습니다."),
    MATCHING_RECEIVER_BAD_REQUEST(BAD_REQUEST, "MATCHING_400_5", "올바르지 않은 수신자 정보를 입력하였습니다."),

    CANNOT_REQUEST_MY_PROFILE(BAD_REQUEST, "MATCHING_400_6", "나의 프로필에 매칭 요청을 보낼 수 없습니다."),
    CANNOT_REQUEST_MY_TEAM(BAD_REQUEST, "MATCHING_400_7", "내가 속한 팀에 매칭 요청을 보낼 수 없습니다."),
    CANNOT_REQUEST_MY_ANNOUNCEMENT(BAD_REQUEST, "MATCHING_400_8", "내가 속한 팀의 공고에 매칭 요청을 보낼 수 없습니다."),
    NOT_ALLOW_MATCHING_BAD_REQUEST(BAD_REQUEST, "MATCHING_400_9", "허용되지 않은 매칭 요청입니다."),
    MATCHING_STATUS_TYPE_BAD_REQUEST(BAD_REQUEST, "MATCHING_400_10", "잘못된 매칭 수락 및 거절 요청입니다."),
    UPDATE_MATCHING_STATUS_TYPE_BAD_REQUEST(BAD_REQUEST, "MATCHING_400_11", "매칭 수신자에 의한 매칭 수락 요청이 아닙니다."),

    MATCHING_NOT_FOUND(NOT_FOUND, "MATCHING_404_1", "해당하는 매칭을 찾을 수 없습니다.");

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
