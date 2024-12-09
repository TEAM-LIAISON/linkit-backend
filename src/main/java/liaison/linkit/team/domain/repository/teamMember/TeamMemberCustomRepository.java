package liaison.linkit.team.domain.repository.teamMember;

import java.util.List;
import liaison.linkit.team.domain.TeamMember;

public interface TeamMemberCustomRepository {
    boolean existsTeamMemberByMemberIdAndTeamId(final Long memberId, final Long teamId);

    List<TeamMember> getTeamMembers(final Long teamId);

    boolean isMemberOfTeam(final Long teamId, final Long memberId);
}
