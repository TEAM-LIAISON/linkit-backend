package liaison.linkit.profile.domain.repository.log;

import java.util.List;
import java.util.Optional;

import liaison.linkit.profile.domain.log.ProfileLog;
import liaison.linkit.profile.presentation.log.dto.ProfileLogRequestDTO.UpdateProfileLogRequest;

public interface ProfileLogCustomRepository {

    List<ProfileLog> getProfileLogs(final Long memberId);

    List<ProfileLog> getProfileLogsPublic(final Long memberId);

    ProfileLog updateProfileLogTypeRepresent(final ProfileLog profileLog);

    void updateProfileLogTypeGeneral(final ProfileLog profileLog);

    Optional<ProfileLog> findRepresentativeProfileLog(final Long profileId);

    boolean existsProfileLogByProfileId(final Long profileId);

    ProfileLog updateProfileLog(
            final ProfileLog profileLog, final UpdateProfileLogRequest updateProfileLogRequest);

    ProfileLog updateProfileLogPublicState(
            final ProfileLog profileLog, final boolean isProfileLogCurrentPublicState);

    boolean existsRepresentativeProfileLogByProfile(final Long profileId);

    List<ProfileLog> findTopView(final int limit);

    void deleteAllProfileLogs(final Long profileId);
}
