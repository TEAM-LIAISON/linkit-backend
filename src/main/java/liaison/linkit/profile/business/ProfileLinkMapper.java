package liaison.linkit.profile.business;

import java.util.List;
import liaison.linkit.common.annotation.Mapper;
import liaison.linkit.profile.domain.Profile;
import liaison.linkit.profile.domain.ProfileLink;
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
                .linkType(profileLink.getLinkType())
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

    public ProfileLink toProfileLink(Profile profile, ProfileLinkRequestDTO.AddProfileLinkItem requestItem) {
        return ProfileLink.builder()
                .profile(profile)
                .linkName(requestItem.getLinkName())
                .linkPath(requestItem.getLinkPath())
                .linkType(requestItem.getLinkType())
                .build();
    }
    
}
