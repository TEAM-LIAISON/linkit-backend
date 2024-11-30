package liaison.linkit.profile.domain.repository.log;

import java.util.List;
import liaison.linkit.profile.domain.ProfileLog;
import liaison.linkit.profile.presentation.log.dto.ProfileLogRequestDTO.UpdateProfileLogType;

public interface ProfileLogCustomRepository {

    List<ProfileLog> getProfileLogs(final Long memberId);

    ProfileLog updateProfileLogType(final ProfileLog profileLog, final UpdateProfileLogType updateProfileLogType);

    ProfileLog findRepresentativeProfileLog(final Long profileId);
}
