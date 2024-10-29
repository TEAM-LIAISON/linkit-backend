package liaison.linkit.profile.implement;

import liaison.linkit.common.annotation.Adapter;
import liaison.linkit.profile.domain.repository.log.ProfileLogRepository;
import liaison.linkit.profile.exception.log.ProfileLogNotFoundException;
import liaison.linkit.profile.presentation.log.dto.ProfileLogResponseDTO.ProfileLogItem;
import liaison.linkit.profile.presentation.log.dto.ProfileLogResponseDTO.ProfileLogItems;
import lombok.RequiredArgsConstructor;

@Adapter
@RequiredArgsConstructor
public class ProfileLogQueryAdapter {
    private final ProfileLogRepository profileLogRepository;

    public void existsByMemberIdAndProfileLogId(final Long memberId, final Long profileLogId) {
        if (!profileLogRepository.existsByMemberIdAndProfileLogId(memberId, profileLogId)) {
            throw ProfileLogNotFoundException.EXCEPTION;
        }
    }

    public ProfileLogItems getProfileLogItems(final Long memberId) {
        return profileLogRepository.getProfileLogItems(memberId);
    }

    public ProfileLogItem getProfileLogItem(final Long profileLogId) {
        return profileLogRepository.getProfileLogItem(profileLogId);
    }
}
