package liaison.linkit.profile.dto.response;

import liaison.linkit.member.domain.MemberBasicInform;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class MemberNameResponse {
    private final String memberName;

    public MemberNameResponse() {
        this.memberName = null;
    }

    public static MemberNameResponse getMemberName(final MemberBasicInform memberBasicInform) {
        return new MemberNameResponse(
                memberBasicInform.getMemberName()
        );
    }

}
