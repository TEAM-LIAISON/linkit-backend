package liaison.linkit.profile.dto.response;

import liaison.linkit.profile.domain.Profile;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class ProfileResponse {

    private final String introduction;

    public static ProfileResponse of(final Profile profile) {
        return new ProfileResponse(
                profile.getIntroduction()
        );
    }

    public static ProfileResponse personalProfile(final Profile profile) {
        return new ProfileResponse(
                profile.getIntroduction()
        );
    }
}
