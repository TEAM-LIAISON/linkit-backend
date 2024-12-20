package liaison.linkit.team.presentation.teamMember.dto;

import java.util.ArrayList;
import java.util.List;
import liaison.linkit.common.presentation.RegionResponseDTO.RegionDetail;
import liaison.linkit.profile.presentation.miniProfile.dto.MiniProfileResponseDTO.ProfileCurrentStateItem;
import liaison.linkit.team.domain.teamMember.TeamMemberType;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class TeamMemberResponseDTO {

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class TeamMemberItems {
        @Builder.Default
        private List<ProfileInformMenu> profileInformMenus = new ArrayList<>();
    }

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ProfileInformMenu {
        @Builder.Default
        private List<ProfileCurrentStateItem> profileCurrentStates = new ArrayList<>();

        private String profileImagePath;
        private String memberName;
        private Boolean isProfilePublic;
        private String majorPosition;

        @Builder.Default
        private RegionDetail regionDetail = new RegionDetail();
    }


    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class AddTeamMemberResponse {
        private String invitedTeamMemberEmail;
        private String teamName;
    }

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UpdateTeamMemberTypeResponse {
        private String emailId;
        private TeamMemberType teamMemberType;
    }
}
