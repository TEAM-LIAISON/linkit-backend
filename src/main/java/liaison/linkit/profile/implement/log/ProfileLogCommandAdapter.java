package liaison.linkit.profile.implement.log;

import liaison.linkit.common.annotation.Adapter;
import liaison.linkit.profile.domain.ProfileLog;
import liaison.linkit.profile.domain.repository.log.ProfileLogRepository;
import liaison.linkit.profile.presentation.log.dto.ProfileLogRequestDTO;
import liaison.linkit.profile.presentation.log.dto.ProfileLogRequestDTO.UpdateProfileLogRequest;
import lombok.RequiredArgsConstructor;

@Adapter
@RequiredArgsConstructor
public class ProfileLogCommandAdapter {
    private final ProfileLogRepository profileLogRepository;


    public ProfileLog addProfileLog(final ProfileLog profileLog) {
        return profileLogRepository.save(profileLog);
    }

    public void remove(final ProfileLog profileLog) {
        profileLogRepository.delete(profileLog);
    }

    public ProfileLog updateProfileLogType(final ProfileLog profileLog, final ProfileLogRequestDTO.UpdateProfileLogType updateProfileLogType) {
        return profileLogRepository.updateProfileLogType(profileLog, updateProfileLogType);
    }

    public ProfileLog updateProfileLog(final ProfileLog profileLog, final UpdateProfileLogRequest updateProfileLogRequest) {
        return profileLogRepository.updateProfileLog(profileLog, updateProfileLogRequest);
    }

    public ProfileLog updateProfileLogPublicState(final ProfileLog profileLog, final boolean isProfileLogCurrentPublicState) {
        return profileLogRepository.updateProfileLogPublicState(profileLog, isProfileLogCurrentPublicState);
    }
}
