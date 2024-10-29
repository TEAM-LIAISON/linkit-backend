package liaison.linkit.profile.service;

import static liaison.linkit.profile.exception.log.ProfileLogErrorCode.PROFILE_LOG_NOT_FOUND;

import liaison.linkit.profile.exception.log.ProfileLogNotFoundException;
import liaison.linkit.profile.implement.ProfileLogCommandAdapter;
import liaison.linkit.profile.implement.ProfileLogQueryAdapter;
import liaison.linkit.profile.presentation.log.dto.ProfileLogResponseDTO.ProfileLogItem;
import liaison.linkit.profile.presentation.log.dto.ProfileLogResponseDTO.ProfileLogItems;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class ProfileLogService {

    private final ProfileLogQueryAdapter profileLogQueryAdapter;
    private final ProfileLogCommandAdapter profileLogCommandAdapter;

    @Transactional(readOnly = true)
    public ProfileLogItems getProfileLogItems(final Long memberId) {
        return profileLogQueryAdapter.getProfileLogItems(memberId);
    }

    @Transactional(readOnly = true)
    public ProfileLogItem getProfileLogItem(final Long memberId, final Long profileLogId) {
        profileLogQueryAdapter.existsByMemberIdAndProfileLogId(memberId, profileLogId));
        return profileLogQueryAdapter.getProfileLogItem(profileLogId);
    }
}
