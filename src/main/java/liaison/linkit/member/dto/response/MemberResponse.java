package liaison.linkit.member.dto.response;

import liaison.linkit.member.domain.Member;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class MemberResponse {
    private final String email;

    public static MemberResponse getEmail(final Member member) {
        return new MemberResponse(
                member.getEmail()
        );
    }

}
