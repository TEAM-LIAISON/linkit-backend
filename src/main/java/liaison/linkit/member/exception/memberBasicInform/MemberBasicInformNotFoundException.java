package liaison.linkit.member.exception.memberBasicInform;

import liaison.linkit.common.exception.BaseCodeException;

public class MemberBasicInformNotFoundException extends BaseCodeException {

    public static BaseCodeException NOT_FOUND_EXCEPTION = new MemberBasicInformNotFoundException();

    private MemberBasicInformNotFoundException() {
        super(MemberBasicInformErrorCode.MEMBER_BASIC_INFORM_NOT_FOUND);
    }
}
