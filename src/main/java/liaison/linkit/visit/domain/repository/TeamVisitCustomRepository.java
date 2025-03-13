package liaison.linkit.visit.domain.repository;

import java.util.List;

import liaison.linkit.visit.domain.TeamVisit;

public interface TeamVisitCustomRepository {
    List<TeamVisit> getTeamVisitsByVisitedTeamId(final Long visitedTeamId);
}
