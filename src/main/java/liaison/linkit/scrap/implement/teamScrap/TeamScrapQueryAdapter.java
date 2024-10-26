package liaison.linkit.scrap.implement.teamScrap;

import java.util.List;
import liaison.linkit.common.annotation.Adapter;
import liaison.linkit.scrap.domain.TeamScrap;
import liaison.linkit.scrap.domain.repository.teamScrap.TeamScrapRepository;
import liaison.linkit.scrap.exception.teamScrap.TeamScrapNotFoundException;
import lombok.RequiredArgsConstructor;

@Adapter
@RequiredArgsConstructor
public class TeamScrapQueryAdapter {
    private final TeamScrapRepository teamScrapRepository;

    public List<TeamScrap> findAllByMemberId(final Long memberId) {
        return teamScrapRepository.findAllByMemberId(memberId);
    }

    public TeamScrap findByTeamId(final Long teamId) {
        return teamScrapRepository.findByTeamId(teamId)
                .orElseThrow(() -> TeamScrapNotFoundException.EXCEPTION);
    }
    
    public boolean existsByMemberIdAndTeamId(final Long memberId, final Long teamId) {
        return teamScrapRepository.existsByMemberIdAndTeamId(memberId, teamId);
    }
}
