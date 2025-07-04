package liaison.linkit.visit.implement;

import liaison.linkit.common.annotation.Adapter;
import liaison.linkit.visit.domain.ProfileVisit;
import liaison.linkit.visit.domain.repository.ProfileVisitRepository;
import lombok.RequiredArgsConstructor;

@Adapter
@RequiredArgsConstructor
public class ProfileVisitCommandAdapter {
    private final ProfileVisitRepository profileVisitRepository;

    public void save(final ProfileVisit profileVisit) {
        profileVisitRepository.save(profileVisit);
    }

    public void removeVisitorByVisitorProfileId(final Long visitorProfileId) {
        profileVisitRepository.removeVisitorByVisitorProfileId(visitorProfileId);
    }

    public void removeVisitorByVisitedProfileId(final Long visitedProfileId) {
        profileVisitRepository.removeVisitorByVisitedProfileId(visitedProfileId);
    }

    public void updateVisitTime(final ProfileVisit profileVisit) {
        profileVisitRepository.updateVisitTime(profileVisit);
    }
}
