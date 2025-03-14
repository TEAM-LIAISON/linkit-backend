package liaison.linkit.visit.domain.repository;

import java.util.List;

import liaison.linkit.visit.domain.ProfileVisit;

public interface ProfileVisitCustomRepository {
    List<ProfileVisit> getProfileVisitsByVisitedProfileId(final Long visitedProfileId);

    boolean existsByVisitedProfileIdAndVisitorProfileId(
            final Long visitedProfileId, final Long visitorProfileId);
}
