package liaison.linkit.profile.implement.visit;

import liaison.linkit.common.annotation.Adapter;
import liaison.linkit.profile.domain.repository.visit.ProfileVisitRepository;
import lombok.RequiredArgsConstructor;

@Adapter
@RequiredArgsConstructor
public class ProfileVisitQueryAdapter {
    private final ProfileVisitRepository profileVisitRepository;
}
