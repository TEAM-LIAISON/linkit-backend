package liaison.linkit.visit.implement;

import liaison.linkit.common.annotation.Adapter;
import liaison.linkit.visit.domain.TeamVisit;
import liaison.linkit.visit.domain.repository.TeamVisitRepository;
import lombok.RequiredArgsConstructor;

@Adapter
@RequiredArgsConstructor
public class TeamVisitCommandAdapter {
    private final TeamVisitRepository teamVisitRepository;

    public void save(final TeamVisit teamVisit) {
        teamVisitRepository.save(teamVisit);
    }

    public void removeVisitorByVisitorProfileId(final Long visitorProfileId) {
        teamVisitRepository.removeVisitorByVisitorProfileId(visitorProfileId);
    }

    public void removeVisitorByVisitedTeamId(final Long visitedTeamId) {
        teamVisitRepository.removeVisitorByVisitedTeamId(visitedTeamId);
    }

    public void updateVisitTime(final TeamVisit teamVisit) {
        teamVisitRepository.updateVisitTime(teamVisit);
    }
}
