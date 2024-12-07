package liaison.linkit.team.implement.teamMember;

import java.util.List;
import liaison.linkit.common.annotation.Adapter;
import liaison.linkit.team.domain.TeamMember;
import liaison.linkit.team.domain.repository.teamMember.TeamMemberRepository;
import lombok.RequiredArgsConstructor;

@Adapter
@RequiredArgsConstructor
public class TeamMemberQueryAdapter {
    private final TeamMemberRepository teamMemberRepository;

    public boolean existsTeamMemberByMemberIdAndTeamId(final Long memberId, final Long teamId) {
        return teamMemberRepository.existsTeamMemberByMemberIdAndTeamId(memberId, teamId);
    }

    public List<TeamMember> getTeamMembers(final Long teamId) {
        return teamMemberRepository.getTeamMembers(teamId);
    }

    public boolean isMemberOfTeam(final Long teamId, final Long memberId) {
        return teamMemberRepository.isMemberOfTeam(teamId, memberId);
    }
}
