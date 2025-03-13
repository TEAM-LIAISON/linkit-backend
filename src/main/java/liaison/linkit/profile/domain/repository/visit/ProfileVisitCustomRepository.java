package liaison.linkit.profile.domain.repository.visit;

import java.util.List;

import liaison.linkit.profile.domain.visit.ProfileVisit;

public interface ProfileVisitCustomRepository {
    List<ProfileVisit> getProfileVisitsByVisitedProfileId(final Long visitedProfileId);
}
