package liaison.linkit.profile.implement;

import liaison.linkit.common.annotation.Adapter;
import liaison.linkit.profile.domain.repository.url.ProfileUrlRepository;
import liaison.linkit.profile.presentation.url.dto.ProfileUrlResponseDTO.ProfileUrlItems;
import lombok.RequiredArgsConstructor;

@Adapter
@RequiredArgsConstructor
public class ProfileUrlQueryAdapter {
    private final ProfileUrlRepository profileUrlRepository;

    public ProfileUrlItems getProfileUrlItems(final Long memberId) {
        return profileUrlRepository.findProfileUrlItems(memberId);
    }
}
