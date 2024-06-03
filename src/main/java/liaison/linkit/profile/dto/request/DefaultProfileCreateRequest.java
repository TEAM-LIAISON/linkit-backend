package liaison.linkit.profile.dto.request;

import liaison.linkit.profile.dto.request.antecedents.AntecedentsCreateRequest;
import liaison.linkit.profile.dto.request.education.EducationCreateRequest;
import liaison.linkit.profile.dto.request.skill.ProfileSkillCreateRequest;
import liaison.linkit.profile.dto.request.teamBuilding.ProfileTeamBuildingCreateRequest;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class DefaultProfileCreateRequest {
    private final ProfileTeamBuildingCreateRequest profileTeamBuildingResponse;
    private final ProfileSkillCreateRequest profileSkillCreateRequest;
    private final List<EducationCreateRequest> educationCreateRequest;
    private final List<AntecedentsCreateRequest> antecedentsCreateRequest;
}
