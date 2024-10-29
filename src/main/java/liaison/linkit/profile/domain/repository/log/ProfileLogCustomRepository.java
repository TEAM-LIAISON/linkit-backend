package liaison.linkit.profile.domain.repository.log;

import liaison.linkit.profile.presentation.log.dto.ProfileLogResponseDTO.ProfileLogItem;
import liaison.linkit.profile.presentation.log.dto.ProfileLogResponseDTO.ProfileLogItems;

public interface ProfileLogCustomRepository {

    boolean existsByMemberIdAndProfileLogId(final Long memberId, final Long profileLogId);

    ProfileLogItems getProfileLogItems(final Long memberId);

    ProfileLogItem getProfileLogItem(final Long profileLogId);
}
