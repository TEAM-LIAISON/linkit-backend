package liaison.linkit.profile.dto.request.skill;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class ProfileSkillUpdateRequest {
    private List<String> skillNames;
}
