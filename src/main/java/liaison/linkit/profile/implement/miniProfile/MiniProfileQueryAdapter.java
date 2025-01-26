package liaison.linkit.profile.implement.miniProfile;

import liaison.linkit.common.annotation.Adapter;
import liaison.linkit.profile.domain.repository.profile.ProfileRepository;
import liaison.linkit.profile.presentation.miniProfile.dto.MiniProfileResponseDTO.MiniProfileDetailResponse;
import lombok.RequiredArgsConstructor;

@Adapter
@RequiredArgsConstructor
public class MiniProfileQueryAdapter {

    private final ProfileRepository profileRepository;

    public MiniProfileDetailResponse getMiniProfileDetail(final Long memberId) {
        return profileRepository.findMiniProfileDetail(memberId);
    }
}
