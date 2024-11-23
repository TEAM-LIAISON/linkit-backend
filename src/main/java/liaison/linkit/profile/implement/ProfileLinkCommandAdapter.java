package liaison.linkit.profile.implement;

import java.util.List;
import liaison.linkit.common.annotation.Adapter;
import liaison.linkit.profile.domain.ProfileLink;
import liaison.linkit.profile.domain.repository.link.ProfileLinkRepository;
import lombok.RequiredArgsConstructor;

@Adapter
@RequiredArgsConstructor
public class ProfileLinkCommandAdapter {

    private final ProfileLinkRepository profileLinkRepository;

    public void removeProfileLinksByProfileId(final Long profileId) {
        profileLinkRepository.deleteAllByProfileId(profileId);
    }

    public void addProfileLinks(final List<ProfileLink> profileLinks) {
        profileLinkRepository.saveAll(profileLinks);
    }
}
