package liaison.linkit.profile.implement.awards;

import java.util.List;
import liaison.linkit.common.annotation.Adapter;
import liaison.linkit.profile.domain.ProfileAwards;
import liaison.linkit.profile.domain.repository.awards.ProfileAwardsRepository;
import liaison.linkit.profile.exception.awards.ProfileAwardsNotFoundException;
import lombok.RequiredArgsConstructor;

@Adapter
@RequiredArgsConstructor
public class ProfileAwardsQueryAdapter {

    private final ProfileAwardsRepository profileAwardsRepository;

    public List<ProfileAwards> getProfileAwardsGroup(final Long memberId) {
        return profileAwardsRepository.getProfileAwardsGroup(memberId);
    }

    public ProfileAwards getProfileAwards(final Long profileAwardsId) {
        return profileAwardsRepository.findById(profileAwardsId)
                .orElseThrow(() -> ProfileAwardsNotFoundException.EXCEPTION);
    }
}
