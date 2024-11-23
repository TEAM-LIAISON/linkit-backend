package liaison.linkit.profile.presentation.link.dto;

import java.util.List;
import liaison.linkit.profile.domain.type.LinkType;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ProfileLinkResponseDTO {

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ProfileLinkItem {
        private Long profileLinkId;
        private String linkName;
        private String linkPath;
        private LinkType linkType;
    }

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ProfileLinkItems {
        @Builder.Default
        private List<ProfileLinkResponseDTO.ProfileLinkItem> profileLinkItems = List.of();
    }
}
