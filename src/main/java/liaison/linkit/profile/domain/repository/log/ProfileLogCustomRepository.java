package liaison.linkit.profile.domain.repository.log;

import java.util.List;
import java.util.Optional;
import liaison.linkit.profile.domain.ProfileLog;
import liaison.linkit.profile.presentation.log.dto.ProfileLogRequestDTO.UpdateProfileLogRequest;
import liaison.linkit.profile.presentation.log.dto.ProfileLogRequestDTO.UpdateProfileLogType;

public interface ProfileLogCustomRepository {

    List<ProfileLog> getProfileLogs(final Long memberId);

    ProfileLog updateProfileLogType(final ProfileLog profileLog, final UpdateProfileLogType updateProfileLogType);

    Optional<ProfileLog> findRepresentativeProfileLog(final Long profileId);

    boolean existsProfileLogByProfileId(final Long profileId);

    ProfileLog updateProfileLog(final ProfileLog profileLog, final UpdateProfileLogRequest updateProfileLogRequest);
}
