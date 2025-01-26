package liaison.linkit.chat.exception;

import liaison.linkit.common.exception.BaseCodeException;

public class CreateChatReceiverBadRequestException extends BaseCodeException {
    public static BaseCodeException EXCEPTION = new CreateChatReceiverBadRequestException();

    private CreateChatReceiverBadRequestException() {
        super(ChatErrorCode.CREATE_CHAT_RECEIVER_BAD_REQUEST);
    }
}
