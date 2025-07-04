package liaison.linkit.team.domain.repository.teamMember;

import java.util.List;
import java.util.Map;
import java.util.Set;

import liaison.linkit.member.domain.Member;
import liaison.linkit.profile.presentation.profile.dto.ProfileResponseDTO;
import liaison.linkit.team.domain.team.Team;
import liaison.linkit.team.domain.teamMember.TeamMember;
import liaison.linkit.team.domain.teamMember.type.TeamMemberManagingTeamState;

public interface TeamMemberCustomRepository {

    List<TeamMember> getTeamMembers(final Long teamId);

    Long getTeamOwnerMemberId(final Team team);

    List<Team> getAllTeamsByMemberId(final Long memberId);

    List<Team> getAllPublicTeamsByMemberId(final Long memberId);

    List<Team> getAllTeamsInOwnerStateByMemberId(final Long memberId);

    TeamMember getTeamMemberByTeamCodeAndEmailId(final String teamCode, final String emailId);

    List<Member> findMembersByTeamCode(final String teamCode);

    Member findTeamOwnerByTeamCode(final String teamCode);

    boolean existsTeamMembersExceptOwnerByTeamCode(final String teamCode);

    void removeTeamMemberInTeam(final TeamMember teamMember);

    List<Long> getAllTeamMemberIds(final String teamCode);

    void updateTeamMemberManagingTeamState(
            final TeamMember teamMember,
            final TeamMemberManagingTeamState teamMemberManagingTeamState);

    boolean isMemberOfTeam(final String teamCode, final String emailId);

    List<TeamMember> getAllTeamManagers(final Team team);

    Set<Team> getAllDeletableTeamsByMemberId(final Long memberId);

    void deleteAllTeamMemberByMember(final Long memberId);

    void deleteAllTeamMemberByTeam(final Long teamId);

    boolean isTeamMembersAllowDelete(final Team team);

    boolean isTeamDeleteRequester(final Long memberId, final Long teamId);

    boolean isOwnerOrManagerOfTeam(final Long teamId, final Long memberId);

    boolean existsTeamByMemberId(final Long memberId);

    boolean existsTeamOwnerByMemberId(final Long memberId);

    boolean existsTeamOwnerAndOtherManagerByMemberId(final Long memberId);

    Map<Long, List<ProfileResponseDTO.ProfileTeamInform>> findTeamInformsGroupedByProfile(
            List<Long> profileIds);
}
