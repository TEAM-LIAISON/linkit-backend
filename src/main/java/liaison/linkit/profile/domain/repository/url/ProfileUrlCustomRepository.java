package liaison.linkit.profile.domain.repository.url;

import liaison.linkit.profile.presentation.url.dto.ProfileUrlResponseDTO;

public interface ProfileUrlCustomRepository {
    ProfileUrlResponseDTO.ProfileUrlItems findProfileUrlItems(final Long memberId);
}
