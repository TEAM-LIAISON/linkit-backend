package liaison.linkit.profile.implement.awards;

import liaison.linkit.common.annotation.Adapter;
import liaison.linkit.profile.domain.awards.ProfileAwards;
import liaison.linkit.profile.domain.repository.awards.ProfileAwardsRepository;
import liaison.linkit.profile.presentation.awards.dto.ProfileAwardsRequestDTO.UpdateProfileAwardsRequest;
import lombok.RequiredArgsConstructor;

@Adapter
@RequiredArgsConstructor
public class ProfileAwardsCommandAdapter {
    final ProfileAwardsRepository profileAwardsRepository;

    public ProfileAwards addProfileAwards(final ProfileAwards profileAwards) {
        return profileAwardsRepository.save(profileAwards);
    }

    public void removeProfileAwards(final ProfileAwards profileAwards) {
        profileAwardsRepository.delete(profileAwards);
    }

    public ProfileAwards updateProfileAwards(final Long profileAwardsId, final UpdateProfileAwardsRequest updateProfileAwardsRequest) {
        return profileAwardsRepository.updateProfileAwards(profileAwardsId, updateProfileAwardsRequest);
    }

    public void removeProfileAwardsByProfileId(final Long profileId) {
        profileAwardsRepository.removeProfileAwardsByProfileId(profileId);
    }
}
