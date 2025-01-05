package liaison.linkit.team.domain.repository.teamMember;

import java.util.List;
import liaison.linkit.team.domain.team.Team;
import liaison.linkit.team.domain.teamMember.TeamMember;

public interface TeamMemberCustomRepository {

    List<TeamMember> getTeamMembers(final Long teamId);

    Long getTeamOwnerMemberId(final Team team);

    boolean isMemberOfTeam(final Long teamId, final Long memberId);

    boolean existsTeamByMemberId(final Long memberId);

    boolean existsTeamOwnerByMemberId(final Long memberId);

    List<Team> getAllTeamsByMemberId(final Long memberId);

    List<Team> getAllTeamsInOwnerStateByMemberId(final Long memberId);

    TeamMember getTeamMemberByTeamCodeAndEmailId(final String teamCode, final String emailId);
}
