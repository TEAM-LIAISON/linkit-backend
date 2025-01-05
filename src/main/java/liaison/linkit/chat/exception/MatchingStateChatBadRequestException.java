package liaison.linkit.chat.exception;

import liaison.linkit.common.exception.BaseCodeException;

public class MatchingStateChatBadRequestException extends BaseCodeException {
    public static BaseCodeException EXCEPTION = new MatchingStateChatBadRequestException();

    private MatchingStateChatBadRequestException() {
        super(ChatErrorCode.MATCHING_STATE_CHAT_BAD_REQUEST);
    }
}
