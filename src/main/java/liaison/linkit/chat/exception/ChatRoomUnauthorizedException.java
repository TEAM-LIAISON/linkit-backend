package liaison.linkit.chat.exception;

import liaison.linkit.common.exception.BaseCodeException;

public class ChatRoomUnauthorizedException extends BaseCodeException {

    public static BaseCodeException EXCEPTION = new ChatRoomUnauthorizedException();

    private ChatRoomUnauthorizedException() {
        super(ChatErrorCode.CHAT_ROOM_UNAUTHORIZED);
    }
}
