package liaison.linkit.profile.implement.log;

import java.util.List;

import liaison.linkit.common.annotation.Adapter;
import liaison.linkit.profile.domain.log.ProfileLog;
import liaison.linkit.profile.domain.repository.log.ProfileLogRepository;
import liaison.linkit.profile.exception.log.ProfileLogNotFoundException;
import lombok.RequiredArgsConstructor;

@Adapter
@RequiredArgsConstructor
public class ProfileLogQueryAdapter {

    private final ProfileLogRepository profileLogRepository;

    public ProfileLog getProfileLog(final Long profileLogId) {
        return profileLogRepository
                .findById(profileLogId)
                .orElseThrow(() -> ProfileLogNotFoundException.EXCEPTION);
    }

    public List<ProfileLog> getProfileLogs(final Long memberId) {
        return profileLogRepository.getProfileLogs(memberId);
    }

    public List<ProfileLog> getProfileLogsPublic(final Long memberId) {
        return profileLogRepository.getProfileLogsPublic(memberId);
    }

    public ProfileLog getRepresentativeProfileLog(final Long profileId) {
        return profileLogRepository
                .findRepresentativeProfileLog(profileId)
                .orElseThrow(() -> ProfileLogNotFoundException.EXCEPTION);
    }

    public ProfileLog getRepresentativePublicProfileLog(final Long profileId) {
        return profileLogRepository
                .findRepresentativePublicProfileLog(profileId)
                .orElseThrow(() -> ProfileLogNotFoundException.EXCEPTION);
    }

    public boolean existsProfileLogByProfileId(final Long profileId) {
        return profileLogRepository.existsProfileLogByProfileId(profileId);
    }

    public boolean existsRepresentativeProfileLogByProfile(final Long profileId) {
        return profileLogRepository.existsRepresentativeProfileLogByProfile(profileId);
    }

    public boolean existsRepresentativePublicProfileLogByProfile(final Long profileId) {
        return profileLogRepository.existsRepresentativePublicProfileLogByProfile(profileId);
    }

    public List<ProfileLog> findTopView(final int limit) {
        return profileLogRepository.findTopView(limit);
    }

    public ProfileLog getRandomProfileLog() {
        return profileLogRepository.findRandomProfileLog();
    }
}
