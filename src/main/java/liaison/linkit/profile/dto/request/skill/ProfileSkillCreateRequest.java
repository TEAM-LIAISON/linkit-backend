package liaison.linkit.profile.dto.request.skill;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class ProfileSkillCreateRequest {

    @JsonProperty("profileSkillCreateRequest")
    private List<SkillPair> skillPairs;

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SkillPair {
        @JsonProperty("role_field")
        private String roleField;

        @JsonProperty("skill_name")
        private String skillName;
    }
}
