package liaison.linkit.visit.domain.repository;

import java.util.List;

import liaison.linkit.visit.domain.TeamVisit;

public interface TeamVisitCustomRepository {
    List<TeamVisit> getTeamVisitsByVisitedTeamId(final Long visitedTeamId);

    boolean existsByVisitedTeamIdAndVisitorProfileId(
            final Long visitedTeamId, final Long visitorMemberId);

    void removeVisitorByVisitorProfileId(final Long visitorProfileId);

    void removeVisitorByVisitedTeamId(final Long visitedTeamId);
}
