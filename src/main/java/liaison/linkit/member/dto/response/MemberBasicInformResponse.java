package liaison.linkit.member.dto.response;

import liaison.linkit.member.domain.MemberBasicInform;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class MemberBasicInformResponse {

    private final Long id;
    private final String profileImageName;
    private final String memberName;
    private final String contact;
    private final String roleName;
    private final boolean marketingAgree;

    public static MemberBasicInformResponse of(final MemberBasicInform memberBasicInform) {
        return new MemberBasicInformResponse(
                memberBasicInform.getId(),
                memberBasicInform.getProfileImageName(),
                memberBasicInform.getMemberName(),
                memberBasicInform.getContact(),
                memberBasicInform.getMemberRole().getRoleName(),
                memberBasicInform.isMarketingAgree()
        );
    }
}
