package liaison.linkit.visit.implement;

import java.util.List;

import liaison.linkit.common.annotation.Adapter;
import liaison.linkit.visit.domain.ProfileVisit;
import liaison.linkit.visit.domain.repository.ProfileVisitRepository;
import lombok.RequiredArgsConstructor;

@Adapter
@RequiredArgsConstructor
public class ProfileVisitQueryAdapter {

    private final ProfileVisitRepository profileVisitRepository;

    public List<ProfileVisit> getProfileVisitsByVisitedProfileId(final Long visitedProfileId) {
        return profileVisitRepository.getProfileVisitsByVisitedProfileId(visitedProfileId);
    }

    public boolean existsByVisitedProfileIdAndVisitorProfileId(
            final Long visitedProfileId, final Long visitorProfileId) {
        return profileVisitRepository.existsByVisitedProfileIdAndVisitorProfileId(
                visitedProfileId, visitorProfileId);
    }
}
