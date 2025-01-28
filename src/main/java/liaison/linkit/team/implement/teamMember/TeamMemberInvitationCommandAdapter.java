package liaison.linkit.team.implement.teamMember;


import java.util.List;
import liaison.linkit.common.annotation.Adapter;
import liaison.linkit.team.domain.repository.teamMember.TeamMemberInvitationRepository;
import liaison.linkit.team.domain.teamMember.TeamMemberInvitation;
import lombok.RequiredArgsConstructor;

@Adapter
@RequiredArgsConstructor
public class TeamMemberInvitationCommandAdapter {
    private final TeamMemberInvitationRepository teamMemberInvitationRepository;

    public void addTeamMemberInvitation(final TeamMemberInvitation teamMemberInvitation) {
        teamMemberInvitationRepository.save(teamMemberInvitation);
    }

    public void removeTeamMemberInvitation(final TeamMemberInvitation teamMemberInvitation) {
        teamMemberInvitationRepository.removeTeamMemberInvitation(teamMemberInvitation);
    }

    public void deleteAllByTeamIds(final List<Long> teamIds) {
        teamMemberInvitationRepository.deleteAllByTeamIds(teamIds);
    }

    public void deleteAllByTeamId(final Long teamId) {
        teamMemberInvitationRepository.deleteAllByTeamId(teamId);
    }
}
