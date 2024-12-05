package liaison.linkit.profile.implement.log;

import liaison.linkit.common.annotation.Adapter;
import liaison.linkit.profile.domain.ProfileLog;
import liaison.linkit.profile.domain.ProfileLogImage;
import liaison.linkit.profile.domain.repository.log.ProfileLogImageRepository;
import liaison.linkit.profile.domain.repository.log.ProfileLogRepository;
import liaison.linkit.profile.presentation.log.dto.ProfileLogRequestDTO;
import liaison.linkit.profile.presentation.log.dto.ProfileLogRequestDTO.UpdateProfileLogRequest;
import lombok.RequiredArgsConstructor;

@Adapter
@RequiredArgsConstructor
public class ProfileLogCommandAdapter {
    private final ProfileLogRepository profileLogRepository;
    private final ProfileLogImageRepository profileLogImageRepository;

    public ProfileLog addProfileLog(final ProfileLog profileLog) {
        return profileLogRepository.save(profileLog);
    }

    public void remove(final ProfileLog profileLog) {
        profileLogRepository.delete(profileLog);
    }

    public ProfileLog updateProfileLogType(final ProfileLog profileLog, final ProfileLogRequestDTO.UpdateProfileLogType updateProfileLogType) {
        return profileLogRepository.updateProfileLogType(profileLog, updateProfileLogType);
    }

    public ProfileLogImage addProfileLogImage(final ProfileLogImage profileLogImage) {
        return profileLogImageRepository.save(profileLogImage);
    }

    public ProfileLog updateProfileLog(final ProfileLog profileLog, final UpdateProfileLogRequest updateProfileLogRequest) {
        return profileLogRepository.updateProfileLog(profileLog, updateProfileLogRequest);
    }

    public void removeProfileLogImage(final ProfileLogImage profileLogImage) {
        profileLogImageRepository.delete(profileLogImage);
    }
}
