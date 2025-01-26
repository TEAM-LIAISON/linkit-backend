package liaison.linkit.profile.business.mapper;

import java.util.List;
import java.util.stream.Collectors;
import liaison.linkit.common.annotation.Mapper;
import liaison.linkit.profile.domain.profile.Profile;
import liaison.linkit.profile.domain.link.ProfileLink;
import liaison.linkit.profile.presentation.link.dto.ProfileLinkRequestDTO;
import liaison.linkit.profile.presentation.link.dto.ProfileLinkResponseDTO;
import liaison.linkit.profile.presentation.link.dto.ProfileLinkResponseDTO.ProfileLinkItem;

@Mapper
public class ProfileLinkMapper {
    public ProfileLinkResponseDTO.ProfileLinkItem toProfileLinkItem(final ProfileLink profileLink) {
        return ProfileLinkResponseDTO.ProfileLinkItem
                .builder()
                .profileLinkId(profileLink.getId())
                .linkName(profileLink.getLinkName())
                .linkPath(profileLink.getLinkPath())
                .build();
    }

    public ProfileLinkResponseDTO.ProfileLinkItems toProfileLinkItems(final List<ProfileLink> profileLinks) {
        List<ProfileLinkItem> items = profileLinks.stream()
                .map(this::toProfileLinkItem)
                .toList();

        return ProfileLinkResponseDTO.ProfileLinkItems
                .builder()
                .profileLinkItems(items)
                .build();
    }

    public List<ProfileLinkResponseDTO.ProfileLinkItem> profileLinksToProfileLinkItems(final List<ProfileLink> profileLinks) {
        return profileLinks.stream()
                .map(this::toProfileLinkItem)
                .collect(Collectors.toList());
    }

    public ProfileLink toProfileLink(Profile profile, ProfileLinkRequestDTO.AddProfileLinkItem requestItem) {
        return ProfileLink.builder()
                .profile(profile)
                .linkName(requestItem.getLinkName())
                .linkPath(requestItem.getLinkPath())
                .build();
    }

}
