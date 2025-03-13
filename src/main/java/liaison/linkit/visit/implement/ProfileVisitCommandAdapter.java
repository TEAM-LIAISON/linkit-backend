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
}
