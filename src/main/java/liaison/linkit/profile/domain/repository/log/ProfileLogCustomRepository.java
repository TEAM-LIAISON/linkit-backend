package liaison.linkit.profile.domain.repository.log;

import java.util.List;
import liaison.linkit.profile.domain.ProfileLog;

public interface ProfileLogCustomRepository {

    List<ProfileLog> getProfileLogs(final Long memberId);
}
