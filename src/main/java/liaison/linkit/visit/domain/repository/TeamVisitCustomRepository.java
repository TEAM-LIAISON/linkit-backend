package liaison.linkit.visit.domain.repository;

import java.util.List;
import java.util.Map;

import liaison.linkit.visit.domain.TeamVisit;

public interface TeamVisitCustomRepository {
    List<TeamVisit> getTeamVisitsByVisitedTeamId(final Long visitedTeamId);

    Map<Long, Long> countVisitsPerTeamWithinLastWeek();

    boolean existsByVisitedTeamIdAndVisitorProfileId(
            final Long visitedTeamId, final Long visitorMemberId);

    void removeVisitorByVisitorProfileId(final Long visitorProfileId);

    void removeVisitorByVisitedTeamId(final Long visitedTeamId);

    TeamVisit getTeamVisitByVisitedTeamIdAndVisitorProfileId(
            final Long visitedTeamId, final Long visitorProfileId);

    void updateVisitTime(final TeamVisit teamVisit);
}
