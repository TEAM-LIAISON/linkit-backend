package liaison.linkit.global.presentation.dto;

import java.util.ArrayList;
import java.util.List;

import liaison.linkit.profile.presentation.profile.dto.ProfileResponseDTO;
import liaison.linkit.search.presentation.dto.LogResponseDTO;
import liaison.linkit.team.presentation.announcement.dto.TeamMemberAnnouncementResponseDTO;
import liaison.linkit.team.presentation.team.dto.TeamResponseDTO;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class HomeResponseDTO {

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class HomeResponse {
        @Builder.Default
        private List<TeamMemberAnnouncementResponseDTO.AnnouncementInformMenu>
                announcementInformMenus = new ArrayList<>();

        @Builder.Default
        private List<TeamResponseDTO.TeamInformMenu> teamInformMenus = new ArrayList<>();

        @Builder.Default
        private List<ProfileResponseDTO.ProfileInformMenu> profileInformMenus = new ArrayList<>();

        @Builder.Default
        private List<LogResponseDTO.LogInformMenu> logInformMenus = new ArrayList<>();
    }
}
