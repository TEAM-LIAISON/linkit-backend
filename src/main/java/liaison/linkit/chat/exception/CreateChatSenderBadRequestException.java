package liaison.linkit.chat.exception;

import liaison.linkit.common.exception.BaseCodeException;

public class CreateChatSenderBadRequestException extends BaseCodeException {
    public static BaseCodeException EXCEPTION = new CreateChatSenderBadRequestException();

    private CreateChatSenderBadRequestException() {
        super(ChatErrorCode.CREATE_CHAT_SENDER_BAD_REQUEST);
    }
}
