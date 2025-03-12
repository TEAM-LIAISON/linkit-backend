package liaison.linkit.profile.domain.repository.link;

import java.util.List;

import liaison.linkit.profile.domain.link.ProfileLink;

public interface ProfileLinkCustomRepository {
    List<ProfileLink> getProfileLinks(final Long profileId);

    boolean existsByProfileId(final Long profileId);

    void deleteAllByProfileId(final Long profileId);
}
