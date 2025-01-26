package liaison.linkit.chat.exception;

import liaison.linkit.common.exception.BaseCodeException;

public class SendChatMessageBadRequestException extends BaseCodeException {
    public static BaseCodeException EXCEPTION = new SendChatMessageBadRequestException();

    private SendChatMessageBadRequestException() {
        super(ChatErrorCode.SEND_CHAT_MESSAGE_BAD_REQUEST);
    }
}
