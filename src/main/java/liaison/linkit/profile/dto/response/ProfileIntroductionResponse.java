package liaison.linkit.profile.dto.response;

import liaison.linkit.profile.domain.Profile;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class ProfileIntroductionResponse {

    private final String introduction;

    public ProfileIntroductionResponse() {
        this.introduction = null;
    }

    public static ProfileIntroductionResponse of(final Profile profile) {
        return new ProfileIntroductionResponse(
                profile.getIntroduction()
        );
    }

    public static ProfileIntroductionResponse profileIntroduction(final Profile profile) {
        return new ProfileIntroductionResponse(
                profile.getIntroduction()
        );
    }
}
