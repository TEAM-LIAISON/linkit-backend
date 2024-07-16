package liaison.linkit.team.dto.response;

import liaison.linkit.team.domain.memberIntroduction.TeamMemberIntroduction;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class TeamMemberIntroductionResponse {

    private final Long id;
    private final String teamMemberName;
    private final String teamMemberRole;
    private final String teamMemberIntroductionText;

    public static TeamMemberIntroductionResponse getTeamMemberIntroduction(final TeamMemberIntroduction teamMemberIntroduction) {
        return new TeamMemberIntroductionResponse(
                teamMemberIntroduction.getId(),
                teamMemberIntroduction.getTeamMemberName(),
                teamMemberIntroduction.getTeamMemberRole(),
                teamMemberIntroduction.getTeamMemberIntroductionText()
        );
    }

    public static TeamMemberIntroductionResponse of(final TeamMemberIntroduction teamMemberIntroduction) {
        return new TeamMemberIntroductionResponse(
                teamMemberIntroduction.getId(),
                teamMemberIntroduction.getTeamMemberName(),
                teamMemberIntroduction.getTeamMemberRole(),
                teamMemberIntroduction.getTeamMemberIntroductionText()
        );
    }
}
