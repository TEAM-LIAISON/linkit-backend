package liaison.linkit.member.exception.memberBasicInform;

import liaison.linkit.common.exception.BaseCodeException;

public class MemberBasicInformBadRequestException extends BaseCodeException {
    public static BaseCodeException BAD_REQUEST_EXCEPTION = new MemberBasicInformBadRequestException();

    private MemberBasicInformBadRequestException() {
        super(MemberBasicInformErrorCode.MEMBER_BASIC_INFORM_BAD_REQUEST);
    }
}
