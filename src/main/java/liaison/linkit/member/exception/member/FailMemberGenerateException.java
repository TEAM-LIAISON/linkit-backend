package liaison.linkit.member.exception.member;

import liaison.linkit.common.exception.BaseCodeException;

public class FailMemberGenerateException extends BaseCodeException {
    public static BaseCodeException EXCEPTION = new FailMemberGenerateException();

    private FailMemberGenerateException() {
        super(MemberErrorCode.FAIL_MEMBER_GENERATE);
    }
}
