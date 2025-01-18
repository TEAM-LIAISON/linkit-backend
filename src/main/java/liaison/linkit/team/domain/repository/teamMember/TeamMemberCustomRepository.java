package liaison.linkit.team.domain.repository.teamMember;

import java.util.List;
import liaison.linkit.member.domain.Member;
import liaison.linkit.team.domain.team.Team;
import liaison.linkit.team.domain.teamMember.TeamMember;
import liaison.linkit.team.domain.teamMember.type.TeamMemberManagingTeamState;

public interface TeamMemberCustomRepository {

    List<TeamMember> getTeamMembers(final Long teamId);

    Long getTeamOwnerMemberId(final Team team);

    boolean isOwnerOrManagerOfTeam(final Long teamId, final Long memberId);

    boolean existsTeamByMemberId(final Long memberId);

    boolean existsTeamOwnerByMemberId(final Long memberId);

    List<Team> getAllTeamsByMemberId(final Long memberId);

    List<Team> getAllTeamsInOwnerStateByMemberId(final Long memberId);

    TeamMember getTeamMemberByTeamCodeAndEmailId(final String teamCode, final String emailId);

    List<Member> findMembersByTeamCode(final String teamCode);

    Member findTeamOwnerByTeamCode(final String teamCode);

    boolean existsTeamMembersByTeamCode(final String teamCode);

    void removeTeamMemberInTeam(final TeamMember teamMember);

    List<Long> getAllTeamMemberIds(final String teamCode);

    void updateTeamMemberManagingTeamState(final TeamMember teamMember, final TeamMemberManagingTeamState teamMemberManagingTeamState);
}
