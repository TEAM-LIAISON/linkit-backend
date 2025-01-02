package liaison.linkit.team.presentation.team.dto;

import java.util.ArrayList;
import java.util.List;
import liaison.linkit.common.presentation.RegionResponseDTO.RegionDetail;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class TeamResponseDTO {

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class TeamDetail {
        private Boolean isMyTeam;

        @Builder.Default
        private TeamInformMenu teamInformMenu = new TeamInformMenu();
    }

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class AddTeamResponse {
        private Long teamId;

        private String teamLogoImagePath;

        private String teamName;

        private String teamCode;

        private String teamShortDescription;

        @Builder.Default
        private TeamScaleItem teamScaleItem = new TeamScaleItem();

        @Builder.Default
        private RegionDetail regionDetail = new RegionDetail();

        @Builder.Default
        private List<TeamCurrentStateItem> teamCurrentStates = new ArrayList<>();

        private Boolean isTeamPublic;
    }

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UpdateTeamResponse {
        private Long teamId;

        private String teamLogoImagePath;

        private String teamName;

        private String teamShortDescription;

        @Builder.Default
        private TeamScaleItem teamScaleItem = new TeamScaleItem();

        @Builder.Default
        private RegionDetail regionDetail = new RegionDetail();

        @Builder.Default
        private List<TeamCurrentStateItem> teamCurrentStates = new ArrayList<>();

        private Boolean isTeamPublic;
    }

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class TeamInformMenus {
        @Builder.Default
        private List<TeamResponseDTO.TeamInformMenu> teamInformMenus = new ArrayList<>();
    }

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class TeamInformMenu {

        @Builder.Default
        private List<TeamCurrentStateItem> teamCurrentStates = new ArrayList<>();

        private Boolean isTeamScrap;
        private int teamScrapCount;

        private String teamName;
        private String teamShortDescription;
        private String teamLogoImagePath;


        // 팀 규모 정보
        @Builder.Default
        private TeamScaleItem teamScaleItem = new TeamScaleItem();

        // 지역 정보
        @Builder.Default
        private RegionDetail regionDetail = new RegionDetail();
    }


    // 팀 현재 상태
    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class TeamCurrentStateItem {
        private String teamStateName;
    }

    // 팀 규모 정보
    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class TeamScaleItem {
        private String teamScaleName;
    }

    // 팀 목록 정보
    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class TeamItems {
        @Builder.Default
        final List<TeamInformMenu> teamInformMenus = new ArrayList<>();
    }
}
