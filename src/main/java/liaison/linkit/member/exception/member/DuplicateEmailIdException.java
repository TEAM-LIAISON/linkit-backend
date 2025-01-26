package liaison.linkit.member.exception.member;

import liaison.linkit.common.exception.BaseCodeException;

public class DuplicateEmailIdException extends BaseCodeException {
    public static BaseCodeException EXCEPTION = new DuplicateEmailIdException();

    private DuplicateEmailIdException() {
        super(MemberErrorCode.DUPLICATE_EMAIL_ID);
    }
}
