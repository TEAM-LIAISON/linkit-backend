package liaison.linkit.member.exception;

import liaison.linkit.common.exception.BaseCodeException;

public class MemberBasicInformNotFoundException extends BaseCodeException {
    public static BaseCodeException EXCEPTION = new MemberBasicInformNotFoundException();

    private MemberBasicInformNotFoundException() {
        super(MemberBasicInformErrorCode.MEMBER_BASIC_INFORM_NOT_FOUND);
    }
}
