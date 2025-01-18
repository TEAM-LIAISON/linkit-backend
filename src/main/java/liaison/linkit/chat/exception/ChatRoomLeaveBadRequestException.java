package liaison.linkit.chat.exception;

import liaison.linkit.common.exception.BaseCodeException;

public class ChatRoomLeaveBadRequestException extends BaseCodeException {
    public static BaseCodeException EXCEPTION = new ChatRoomLeaveBadRequestException();

    private ChatRoomLeaveBadRequestException() {
        super(ChatErrorCode.CHAT_ROOM_LEAVE_BAD_REQUEST);
    }
}
