package liaison.linkit.team.implement.teamMember;


import liaison.linkit.common.annotation.Adapter;
import liaison.linkit.team.domain.Team;
import liaison.linkit.team.domain.repository.teamMember.TeamMemberInvitationRepository;
import lombok.RequiredArgsConstructor;

@Adapter
@RequiredArgsConstructor
public class TeamMemberInvitationQueryAdapter {

    private final TeamMemberInvitationRepository teamMemberInvitationRepository;

    public boolean existsByEmailAndTeam(final String email, final Team team) {
        return teamMemberInvitationRepository.existsByEmailAndTeam(email, team);
    }

}
