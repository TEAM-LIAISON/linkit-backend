package liaison.linkit.profile.dto.response.skill;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.List;

@Getter
@RequiredArgsConstructor
public class ProfileSkillResponse {
    private final List<String> roleFields;
    private final List<String> skillNames;

    public static ProfileSkillResponse of(final List<String> roleFields, final List<String> skillNames) {
        return new ProfileSkillResponse(roleFields, skillNames);
    }
}
