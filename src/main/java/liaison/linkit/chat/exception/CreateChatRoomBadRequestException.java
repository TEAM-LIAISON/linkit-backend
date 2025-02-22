package liaison.linkit.chat.exception;

import liaison.linkit.common.exception.BaseCodeException;

public class CreateChatRoomBadRequestException extends BaseCodeException {
    public static BaseCodeException EXCEPTION = new CreateChatRoomBadRequestException();

    private CreateChatRoomBadRequestException() {
        super(ChatErrorCode.CREATE_CHAT_ROOM_BAD_REQUEST);
    }
}
