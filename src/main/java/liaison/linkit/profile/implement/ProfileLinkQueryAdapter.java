package liaison.linkit.profile.implement;

import java.util.List;
import liaison.linkit.common.annotation.Adapter;
import liaison.linkit.profile.domain.ProfileLink;
import liaison.linkit.profile.domain.repository.link.ProfileLinkRepository;
import lombok.RequiredArgsConstructor;

@Adapter
@RequiredArgsConstructor
public class ProfileLinkQueryAdapter {
    private final ProfileLinkRepository profileLinkRepository;

    public List<ProfileLink> getProfileLinks(final Long profileId) {
        return profileLinkRepository.getProfileLinks(profileId);
    }

    public boolean existsByProfileId(final Long profileId) {
        return profileLinkRepository.existsByProfileId(profileId);
    }
}

