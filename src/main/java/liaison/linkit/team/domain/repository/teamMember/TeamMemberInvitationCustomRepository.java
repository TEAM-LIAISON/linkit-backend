package liaison.linkit.team.domain.repository.teamMember;

import java.util.List;
import liaison.linkit.team.domain.team.Team;
import liaison.linkit.team.domain.teamMember.TeamMemberInvitation;

public interface TeamMemberInvitationCustomRepository {

    boolean existsByEmail(final String email);

    boolean existsByEmailAndTeam(final String email, final Team team);

    List<TeamMemberInvitation> getTeamMemberInvitations(final Long teamId);

    List<Team> getTeamsByEmail(final String email);

    TeamMemberInvitation getTeamMemberInvitationInPendingState(final String email, final Team team);

    TeamMemberInvitation getTeamMemberInvitationByTeamCodeAndEmail(final String teamCode, final String email);

    void removeTeamMemberInvitation(final TeamMemberInvitation teamMemberInvitation);
}
