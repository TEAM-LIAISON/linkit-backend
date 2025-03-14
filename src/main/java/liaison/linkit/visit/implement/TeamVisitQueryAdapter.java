package liaison.linkit.visit.implement;

import java.util.List;

import liaison.linkit.common.annotation.Adapter;
import liaison.linkit.visit.domain.TeamVisit;
import liaison.linkit.visit.domain.repository.TeamVisitRepository;
import lombok.RequiredArgsConstructor;

@Adapter
@RequiredArgsConstructor
public class TeamVisitQueryAdapter {
    private final TeamVisitRepository teamVisitRepository;

    public List<TeamVisit> getTeamVisitsByVisitedTeamId(final Long visitedTeamId) {
        return teamVisitRepository.getTeamVisitsByVisitedTeamId(visitedTeamId);
    }

    public boolean existsByVisitedTeamIdAndVisitorProfileId(
            final Long visitedTeamId, final Long visitorProfileId) {
        return teamVisitRepository.existsByVisitedTeamIdAndVisitorProfileId(
                visitedTeamId, visitorProfileId);
    }
}
