package liaison.linkit.team.implement.teamMember;

import liaison.linkit.common.annotation.Adapter;
import liaison.linkit.team.domain.teamMember.TeamMember;
import liaison.linkit.team.domain.repository.teamMember.TeamMemberRepository;
import liaison.linkit.team.domain.teamMember.type.TeamMemberManagingTeamState;
import lombok.RequiredArgsConstructor;

@Adapter
@RequiredArgsConstructor
public class TeamMemberCommandAdapter {
    private final TeamMemberRepository teamMemberRepository;

    public TeamMember addTeamMember(final TeamMember teamMember) {
        return teamMemberRepository.save(teamMember);
    }

    public void removeTeamMemberInTeam(final TeamMember teamMember) {
        teamMemberRepository.removeTeamMemberInTeam(teamMember);
    }

    public void updateTeamMemberManagingTeamState(final TeamMember teamMember, final TeamMemberManagingTeamState teamMemberManagingTeamState) {
        teamMemberRepository.updateTeamMemberManagingTeamState(teamMember, teamMemberManagingTeamState);
    }
}
