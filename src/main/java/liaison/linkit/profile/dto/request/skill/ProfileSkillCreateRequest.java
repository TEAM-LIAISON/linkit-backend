package liaison.linkit.profile.dto.request.skill;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class ProfileSkillCreateRequest {

    private List<String> roleFields;
    private List<String> skillNames;

    public static ProfileSkillCreateRequest of(
            final List<String> roleFields,
            final List<String> skillNames
    ) {
        return new ProfileSkillCreateRequest(roleFields, skillNames);
    }

}
