package liaison.linkit.profile.domain.repository.awards;

import java.util.List;

import liaison.linkit.profile.domain.awards.ProfileAwards;
import liaison.linkit.profile.presentation.awards.dto.ProfileAwardsRequestDTO.UpdateProfileAwardsRequest;

public interface ProfileAwardsCustomRepository {
    List<ProfileAwards> getProfileAwardsGroup(final Long memberId);

    ProfileAwards updateProfileAwards(
            final Long profileAwardsId,
            final UpdateProfileAwardsRequest updateProfileActivityRequest);

    boolean existsByProfileId(final Long profileId);

    void removeProfileAwardsByProfileId(final Long profileId);
}
