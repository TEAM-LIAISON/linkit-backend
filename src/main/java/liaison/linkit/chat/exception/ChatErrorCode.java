package liaison.linkit.chat.exception;

import static liaison.linkit.common.consts.LinkitStatic.BAD_REQUEST;
import static liaison.linkit.common.consts.LinkitStatic.NOT_FOUND;
import static liaison.linkit.common.consts.LinkitStatic.UNAUTHORIZED;

import java.lang.reflect.Field;
import java.util.Objects;

import liaison.linkit.common.annotation.ExplainError;
import liaison.linkit.common.exception.BaseErrorCode;
import liaison.linkit.common.exception.ErrorReason;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ChatErrorCode implements BaseErrorCode {
    MATCHING_STATE_CHAT_BAD_REQUEST(
            BAD_REQUEST, "CHATTING_400_1", "성사되지 않은 매칭에 대하여 채팅방을 생성할 수 없습니다."),
    CREATE_CHAT_SENDER_BAD_REQUEST(
            BAD_REQUEST, "CHATTING_400_2", "발신함에서 채팅방을 생성하는 과정에서 잘못된 요청이 발생했습니다."),
    CREATE_CHAT_RECEIVER_BAD_REQUEST(
            BAD_REQUEST, "CHATTING_400_3", "수신함에서 채팅방을 생성하는 과정에서 잘못된 요청이 발생했습니다."),
    CREATE_CHAT_ROOM_BAD_REQUEST(
            BAD_REQUEST, "CHATTING_400_4", "채팅방이 이미 존재합니다. 새롭게 채팅방을 생성할 수 없습니다."),
    SEND_CHAT_MESSAGE_BAD_REQUEST(BAD_REQUEST, "CHATTING_400_5", "올바르지 않은 채팅 메시지 전송입니다."),
    CHAT_ROOM_LEAVE_BAD_REQUEST(BAD_REQUEST, "CHATTING_400_6", "올바르지 않은 채팅방 나가기 요청입니다."),
    CHAT_ROOM_UNAUTHORIZED(UNAUTHORIZED, "CHATTING_401_1", "해당 채팅방에 접근할 수 없습니다."),
    CHAT_ROOM_NOT_FOUND(NOT_FOUND, "CHATTING_404_1", "조회하고자 하는 채팅방을 찾을 수 없습니다.");

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
