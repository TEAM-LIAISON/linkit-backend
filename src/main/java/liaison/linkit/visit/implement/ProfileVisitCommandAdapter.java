package liaison.linkit.visit.implement;

import liaison.linkit.common.annotation.Adapter;
import liaison.linkit.visit.domain.repository.ProfileVisitRepository;
import liaison.linkit.visit.infrastructure.VisitorRedisUtil;
import lombok.RequiredArgsConstructor;

@Adapter
@RequiredArgsConstructor
public class ProfileVisitCommandAdapter {
    private final ProfileVisitRepository profileVisitRepository;
    private final VisitorRedisUtil visitorRedisUtil;
}
