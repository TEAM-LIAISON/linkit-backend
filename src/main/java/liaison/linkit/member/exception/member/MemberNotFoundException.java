package liaison.linkit.member.exception.member;

import liaison.linkit.common.exception.BaseCodeException;

public class MemberNotFoundException extends BaseCodeException {

    public static BaseCodeException EXCEPTION = new MemberNotFoundException();

    private MemberNotFoundException() {
        super(MemberErrorCode.MEMBER_NOT_FOUND);
    }
}
