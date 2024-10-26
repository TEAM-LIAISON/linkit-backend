package liaison.linkit.team.implement.teamMember;

import liaison.linkit.common.annotation.Adapter;
import liaison.linkit.team.domain.TeamMember;
import liaison.linkit.team.domain.repository.teamMember.TeamMemberRepository;
import lombok.RequiredArgsConstructor;

@Adapter
@RequiredArgsConstructor
public class TeamMemberCommandAdapter {
    private final TeamMemberRepository teamMemberRepository;

    public TeamMember add(final TeamMember teamMember) {
        return teamMemberRepository.save(teamMember);
    }
}
