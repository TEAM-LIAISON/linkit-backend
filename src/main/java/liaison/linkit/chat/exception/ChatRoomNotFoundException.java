package liaison.linkit.chat.exception;

import liaison.linkit.common.exception.BaseCodeException;

public class ChatRoomNotFoundException extends BaseCodeException {
    public static BaseCodeException EXCEPTION = new ChatRoomNotFoundException();

    private ChatRoomNotFoundException() {
        super(ChatErrorCode.CHAT_ROOM_NOT_FOUND);
    }
}
