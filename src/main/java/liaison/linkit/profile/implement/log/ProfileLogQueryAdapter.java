package liaison.linkit.profile.implement.log;

import java.time.LocalDateTime;
import java.util.List;
import liaison.linkit.common.annotation.Adapter;
import liaison.linkit.profile.domain.ProfileLog;
import liaison.linkit.profile.domain.ProfileLogImage;
import liaison.linkit.profile.domain.repository.log.ProfileLogImageRepository;
import liaison.linkit.profile.domain.repository.log.ProfileLogRepository;
import liaison.linkit.profile.exception.log.ProfileLogNotFoundException;
import lombok.RequiredArgsConstructor;

@Adapter
@RequiredArgsConstructor
public class ProfileLogQueryAdapter {
    private final ProfileLogRepository profileLogRepository;
    private final ProfileLogImageRepository profileLogImageRepository;

    public ProfileLog getProfileLog(final Long profileLogId) {
        return profileLogRepository.findById(profileLogId)
                .orElseThrow(() -> ProfileLogNotFoundException.EXCEPTION);
    }

    public List<ProfileLog> getProfileLogs(final Long memberId) {
        return profileLogRepository.getProfileLogs(memberId);
    }

    public ProfileLog getRepresentativeProfileLog(final Long profileId) {
        return profileLogRepository.findRepresentativeProfileLog(profileId)
                .orElseThrow(() -> ProfileLogNotFoundException.EXCEPTION);
    }

    public boolean existsProfileLogByProfileId(final Long profileId) {
        return profileLogRepository.existsProfileLogByProfileId(profileId);
    }

    public List<ProfileLogImage> getUnusedProfileLogImages(LocalDateTime threshold) {
        return profileLogImageRepository.getUnusedProfileLogImages(threshold);
    }
}
