package liaison.linkit.team.implement.teamMember;

import java.util.List;
import liaison.linkit.common.annotation.Adapter;
import liaison.linkit.member.domain.Member;
import liaison.linkit.team.domain.repository.teamMember.TeamMemberRepository;
import liaison.linkit.team.domain.team.Team;
import liaison.linkit.team.domain.teamMember.TeamMember;
import lombok.RequiredArgsConstructor;

@Adapter
@RequiredArgsConstructor
public class TeamMemberQueryAdapter {
    private final TeamMemberRepository teamMemberRepository;

    public List<TeamMember> getTeamMembers(final Long teamId) {
        return teamMemberRepository.getTeamMembers(teamId);
    }

    public Long getTeamOwnerMemberId(final Team team) {
        return teamMemberRepository.getTeamOwnerMemberId(team);
    }

    public boolean isOwnerOrManagerOfTeam(final Long teamId, final Long memberId) {
        return teamMemberRepository.isOwnerOrManagerOfTeam(teamId, memberId);
    }

    public boolean existsTeamByMemberId(final Long memberId) {
        return teamMemberRepository.existsTeamByMemberId(memberId);
    }

    public boolean existsTeamOwnerByMemberId(final Long memberId) {
        return teamMemberRepository.existsTeamOwnerByMemberId(memberId);
    }

    public List<Team> getAllTeamsByMemberId(final Long memberId) {
        return teamMemberRepository.getAllTeamsByMemberId(memberId);
    }

    public List<Team> getAllTeamsInOwnerStateByMemberId(final Long memberId) {
        return teamMemberRepository.getAllTeamsInOwnerStateByMemberId(memberId);
    }

    public TeamMember getTeamMemberByTeamCodeAndEmailId(final String teamCode, final String emailId) {
        return teamMemberRepository.getTeamMemberByTeamCodeAndEmailId(teamCode, emailId);
    }

    public List<Member> findMembersByTeamCode(final String teamCode) {
        return teamMemberRepository.findMembersByTeamCode(teamCode);
    }

    public Member findTeamOwnerByTeamCode(final String teamCode) {
        return teamMemberRepository.findTeamOwnerByTeamCode(teamCode);
    }

    public boolean existsTeamMembersExceptOwnerByTeamCode(final String teamCode) {
        return teamMemberRepository.existsTeamMembersExceptOwnerByTeamCode(teamCode);
    }

    public List<Long> getAllTeamMemberIds(final String teamCode) {
        return teamMemberRepository.getAllTeamMemberIds(teamCode);
    }

    public boolean isMemberOfTeam(final String teamCode, final String emailId) {
        return teamMemberRepository.isMemberOfTeam(teamCode, emailId);
    }

    public List<TeamMember> getAllTeamManagers(final Team team) {
        return teamMemberRepository.getAllTeamManagers(team);
    }
}
