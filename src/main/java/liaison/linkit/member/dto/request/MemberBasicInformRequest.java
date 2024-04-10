package liaison.linkit.member.dto.request;

import lombok.Getter;

@Getter

public class MemberBasicInformRequest {
    private final String memberName;
    private final String contact;
    private final MemberRoleRequest memberRole;
    private final boolean marketingAgree;

    public MemberBasicInformRequest(
            final String memberName,
            final String contact,
            final MemberRoleRequest memberRole,
            boolean marketingAgree
    ) {
        this.memberName = memberName;
        this.contact = contact;
        this.memberRole = memberRole;
        this.marketingAgree = marketingAgree;
    }
}
