package liaison.linkit.team.domain.repository.teamMember;

import java.util.List;
import liaison.linkit.team.domain.Team;
import liaison.linkit.team.domain.teamMember.TeamMemberInvitation;

public interface TeamMemberInvitationCustomRepository {
    boolean existsByEmailAndTeam(final String email, final Team team);

    List<TeamMemberInvitation> getTeamMemberInvitations(final Long teamId);
}
