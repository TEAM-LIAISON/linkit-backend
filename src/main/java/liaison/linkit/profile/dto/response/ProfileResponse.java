package liaison.linkit.profile.dto.response;

import liaison.linkit.member.domain.Member;
import liaison.linkit.profile.domain.Profile;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import static lombok.AccessLevel.PRIVATE;

@Getter
@RequiredArgsConstructor(access = PRIVATE)
public class ProfileResponse {

    private final Long id;
    private final Member member;
    private final String introduction;

    public static ProfileResponse of(final Profile profile) {
        return new ProfileResponse(
                profile.getId(),
                profile.getMember(),
                profile.getIntroduction()
        );
    }

    public static ProfileResponse personalProfile(final Profile profile) {
        return new ProfileResponse(
                profile.getId(),
                profile.getMember(),
                profile.getIntroduction()
        );
    }
}
