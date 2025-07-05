package liaison.linkit.visit.domain.repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import liaison.linkit.visit.domain.ProfileVisit;

public interface ProfileVisitCustomRepository {
    List<ProfileVisit> getProfileVisitsByVisitedProfileId(final Long visitedProfileId);

    List<ProfileVisit> getOneWeekAgoProfileVisitsProfileVisits(final LocalDateTime oneWeekAgo);

    boolean existsByVisitedProfileIdAndVisitorProfileId(
            final Long visitedProfileId, final Long visitorProfileId);

    void removeVisitorByVisitorProfileId(final Long visitorProfileId);

    void removeVisitorByVisitedProfileId(final Long visitedProfileId);

    ProfileVisit getProfileVisitByVisitedProfileIdAndVisitorProfileId(
            final Long visitedProfileId, final Long visitorProfileId);

    void updateVisitTime(final ProfileVisit profileVisit);

    Map<Long, Long> countVisitsPerProfileWithinLastWeek();
}
