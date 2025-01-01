package liaison.linkit.team.implement.teamMember;

import java.util.List;
import liaison.linkit.common.annotation.Adapter;
import liaison.linkit.team.domain.team.Team;
import liaison.linkit.team.domain.teamMember.TeamMember;
import liaison.linkit.team.domain.repository.teamMember.TeamMemberRepository;
import lombok.RequiredArgsConstructor;

@Adapter
@RequiredArgsConstructor
public class TeamMemberQueryAdapter {
    private final TeamMemberRepository teamMemberRepository;

    public List<TeamMember> getTeamMembers(final Long teamId) {
        return teamMemberRepository.getTeamMembers(teamId);
    }

    public boolean isMemberOfTeam(final Long teamId, final Long memberId) {
        return teamMemberRepository.isMemberOfTeam(teamId, memberId);
    }

    public boolean existsTeamByMemberId(final Long memberId) {
        return teamMemberRepository.existsTeamByMemberId(memberId);
    }

    public List<Team> getAllTeamByMemberId(final Long memberId) {
        return teamMemberRepository.getAllTeamByMemberId(memberId);
    }

    public List<Team> getAllTeamsByMemberId(final Long memberId) {
        return teamMemberRepository.getAllTeamsByMemberId(memberId);
    }

    public TeamMember getTeamMemberByTeamNameAndEmailId(final String teamName, final String emailId) {
        return teamMemberRepository.getTeamMemberByTeamNameAndEmailId(teamName, emailId);
    }
}
