package liaison.linkit.team.implement.teamMember;

import java.util.List;

import liaison.linkit.common.annotation.Adapter;
import liaison.linkit.team.domain.repository.teamMember.TeamMemberInvitationRepository;
import liaison.linkit.team.domain.team.Team;
import liaison.linkit.team.domain.teamMember.TeamMemberInvitation;
import lombok.RequiredArgsConstructor;

@Adapter
@RequiredArgsConstructor
public class TeamMemberInvitationQueryAdapter {

    private final TeamMemberInvitationRepository teamMemberInvitationRepository;

    public List<Team> getTeamsByEmail(final String email) {
        return teamMemberInvitationRepository.getTeamsByEmail(email);
    }

    public boolean existsByEmail(final String email) {
        return teamMemberInvitationRepository.existsByEmail(email);
    }

    public boolean existsByEmailAndTeam(final String email, final Team team) {
        return teamMemberInvitationRepository.existsByEmailAndTeam(email, team);
    }

    public List<TeamMemberInvitation> getTeamMemberInvitationsInPending(final Long teamId) {
        return teamMemberInvitationRepository.getTeamMemberInvitationsInPending(teamId);
    }

    public TeamMemberInvitation getTeamMemberInvitationInPendingState(
            final String email, final Team team) {
        return teamMemberInvitationRepository.getTeamMemberInvitationInPendingState(email, team);
    }

    public TeamMemberInvitation getTeamMemberInvitationByTeamCodeAndEmail(
            final String teamCode, final String email) {
        return teamMemberInvitationRepository.getTeamMemberInvitationByTeamCodeAndEmail(
                teamCode, email);
    }
}
