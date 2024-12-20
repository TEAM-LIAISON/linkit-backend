package liaison.linkit.team.domain.repository.teamMember;

import liaison.linkit.team.domain.Team;

public interface TeamMemberInvitationCustomRepository {
    boolean existsByEmailAndTeam(final String email, final Team team);
}
