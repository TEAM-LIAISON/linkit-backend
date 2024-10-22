package liaison.linkit.team.implement.teamMember;

import liaison.linkit.common.annotation.Adapter;
import liaison.linkit.team.domain.repository.teamMember.TeamMemberRepository;
import lombok.RequiredArgsConstructor;

@Adapter
@RequiredArgsConstructor
public class TeamMemberQueryAdapter {
    private final TeamMemberRepository teamMemberRepository;

    public boolean existsTeamMemberByMemberIdAndTeamId(final Long memberId, final Long teamId) {
        return teamMemberRepository.existsTeamMemberByMemberIdAndTeamId(memberId, teamId);
    }

//    public TeamMember findByMemberIdAndTeamId(final Long memberId, final Long teamId) {
//        return teamMemberRepository.findByMemberIdAndTeamId(memberId, teamId);
//    }
}
