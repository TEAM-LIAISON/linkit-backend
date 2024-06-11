package liaison.linkit.member.dto.request.memberBasicInform;

import lombok.Getter;

@Getter
public class MemberBasicInformUpdateRequest {

    private final String memberName;
    private final String contact;
    private final boolean marketingAgree;

    public MemberBasicInformUpdateRequest(
            final String memberName,
            final String contact,
            boolean marketingAgree
    ) {
        this.memberName = memberName;
        this.contact = contact;
        this.marketingAgree = marketingAgree;
    }
}
