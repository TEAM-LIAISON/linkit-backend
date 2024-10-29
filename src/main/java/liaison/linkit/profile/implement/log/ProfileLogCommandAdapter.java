package liaison.linkit.profile.implement.log;

import liaison.linkit.common.annotation.Adapter;
import liaison.linkit.profile.domain.repository.log.ProfileLogRepository;
import lombok.RequiredArgsConstructor;

@Adapter
@RequiredArgsConstructor
public class ProfileLogCommandAdapter {
    private final ProfileLogRepository profileLogRepository;
}
