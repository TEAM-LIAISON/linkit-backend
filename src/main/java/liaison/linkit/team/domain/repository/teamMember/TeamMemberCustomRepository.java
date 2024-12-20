package liaison.linkit.team.domain.repository.teamMember;

import java.util.List;
import liaison.linkit.team.domain.Team;
import liaison.linkit.team.domain.teamMember.TeamMember;

public interface TeamMemberCustomRepository {
    boolean existsTeamMemberByMemberIdAndTeamId(final Long memberId, final Long teamId);

    List<TeamMember> getTeamMembers(final Long teamId);

    boolean isMemberOfTeam(final Long teamId, final Long memberId);

    boolean existsTeamByMemberId(final Long memberId);

    List<Team> getAllTeamByMemberId(final Long memberId);

    List<Team> getAllTeamsByMemberId(final Long memberId);

    TeamMember getTeamMemberByTeamNameAndEmailId(final String teamName, final String emailId);
}
