package liaison.linkit.profile.dto.request.skill;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class ProfileSkillCreateRequest {

    private List<SkillPair> skillPairs;

    public static ProfileSkillCreateRequest of(final List<SkillPair> skillPairs) {
        return new ProfileSkillCreateRequest(skillPairs);
    }

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SkillPair {
        private String roleField;
        private String skillName;
    }
}
