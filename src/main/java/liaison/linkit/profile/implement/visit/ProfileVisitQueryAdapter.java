package liaison.linkit.profile.implement.visit;

import java.util.List;

import liaison.linkit.common.annotation.Adapter;
import liaison.linkit.profile.domain.repository.visit.ProfileVisitRepository;
import liaison.linkit.profile.domain.visit.ProfileVisit;
import lombok.RequiredArgsConstructor;

@Adapter
@RequiredArgsConstructor
public class ProfileVisitQueryAdapter {

    private final ProfileVisitRepository profileVisitRepository;

    public List<ProfileVisit> getProfileVisitsByVisitedProfileId(final Long visitedProfileId) {
        return profileVisitRepository.getProfileVisitsByVisitedProfileId(visitedProfileId);
    }
}
