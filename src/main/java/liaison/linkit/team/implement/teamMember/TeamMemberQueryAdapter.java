package liaison.linkit.team.implement.teamMember;

import liaison.linkit.common.annotation.Adapter;
import liaison.linkit.team.domain.repository.teamMember.TeamMemberRepository;
import lombok.RequiredArgsConstructor;

@Adapter
@RequiredArgsConstructor
public class TeamMemberQueryAdapter {
    private final TeamMemberRepository teamMemberRepository;

    public boolean existsTeamMemberByMemberId(final Long memberId, final Long teamId) {
        return teamMemberRepository.existsTeamMemberByMemberId(memberId, teamId);
    }
}
