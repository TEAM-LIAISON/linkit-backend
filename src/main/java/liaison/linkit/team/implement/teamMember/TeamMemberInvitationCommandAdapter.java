package liaison.linkit.team.implement.teamMember;


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

}
