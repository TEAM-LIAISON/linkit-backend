package liaison.linkit.profile.dto.response;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.List;

@Getter
@RequiredArgsConstructor
public class ProfileSkillResponse {
    private final List<SkillPair> skillPairs;

    @Getter
    @RequiredArgsConstructor
    public static class SkillPair {
        private final String roleField;
        private final String skillName;
    }

    public static ProfileSkillResponse of(final List<SkillPair> skillPairs) {
        return new ProfileSkillResponse(skillPairs);
    }
}
