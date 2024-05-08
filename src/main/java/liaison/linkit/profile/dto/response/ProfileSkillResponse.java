package liaison.linkit.profile.dto.response;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.List;

@Getter
@RequiredArgsConstructor
public class ProfileSkillResponse {
    private final List<String> profileSkillNames;

    public static ProfileSkillResponse of(final List<String> profileSkillNames) {
        return new ProfileSkillResponse(
                profileSkillNames
        );
    }
}
