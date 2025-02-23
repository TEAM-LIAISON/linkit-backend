package liaison.linkit.team.presentation.announcement.dto;

import java.util.ArrayList;
import java.util.List;

import liaison.linkit.common.presentation.RegionResponseDTO.RegionDetail;
import liaison.linkit.team.presentation.team.dto.TeamResponseDTO.TeamScaleItem;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class TeamMemberAnnouncementResponseDTO {

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class AnnouncementInformMenus {
        @Builder.Default
        private List<TeamMemberAnnouncementResponseDTO.AnnouncementInformMenu>
                announcementInformMenus = new ArrayList<>();
    }

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class AnnouncementInformMenu {
        private Long teamMemberAnnouncementId;

        private String teamLogoImagePath;
        private String teamName;
        private String teamCode;

        @Builder.Default private TeamScaleItem teamScaleItem = new TeamScaleItem();

        @Builder.Default private RegionDetail regionDetail = new RegionDetail();

        private int announcementDDay; // 디데이
        private Boolean isClosed;
        private Boolean isPermanentRecruitment; // 상시 모집 여부
        private String announcementTitle; // 공고 제목

        private Boolean isAnnouncementScrap; // 공고 스크랩 여부
        private int announcementScrapCount; // 공고 스크랩 수

        @Builder.Default
        private AnnouncementPositionItem announcementPositionItem = new AnnouncementPositionItem();

        @Builder.Default
        private List<TeamMemberAnnouncementResponseDTO.AnnouncementSkillName>
                announcementSkillNames = new ArrayList<>();
    }

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class TeamMemberAnnouncementDetail {
        private Long teamMemberAnnouncementId;

        private Boolean isAnnouncementScrap;
        private int announcementScrapCount;

        private int announcementDDay; // 디데이
        private Boolean isClosed;
        private Boolean isPermanentRecruitment; // 상시 모집 여부
        private String announcementTitle;

        @Builder.Default
        private AnnouncementPositionItem announcementPositionItem = new AnnouncementPositionItem();

        @Builder.Default
        private List<TeamMemberAnnouncementResponseDTO.AnnouncementSkillName>
                announcementSkillNames = new ArrayList<>();

        private String announcementEndDate;

        private Boolean isRegionFlexible; // 지역 무관
        private String mainTasks; // 주요 업무
        private String workMethod; // 업무 방식
        private String idealCandidate; // 이런 분을 찾고 있어요

        // 필수 기입 아님
        private String preferredQualifications; // 이런 분이면 더 좋아요
        private String joiningProcess; // 이런 과정으로 합류해요
        private String benefits; // 합류하면 이런 것들을 얻어 갈 수 있어요
    }

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class TeamMemberAnnouncementItems {
        @Builder.Default
        private List<TeamMemberAnnouncementItem> teamMemberAnnouncementItems = new ArrayList<>();
    }

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class TeamMemberAnnouncementItem {
        private Long teamMemberAnnouncementId;

        private int announcementDDay;
        private Boolean isClosed;
        private Boolean isPermanentRecruitment; // 상시 모집 여부
        private String announcementTitle;
        private String majorPosition;

        @Builder.Default
        private List<TeamMemberAnnouncementResponseDTO.AnnouncementSkillName>
                announcementSkillNames = new ArrayList<>();

        private Boolean isAnnouncementPublic; // 공고 공개/비공개 여부
        private Boolean isAnnouncementInProgress; // 공고 현재 진행 여부

        private Boolean isAnnouncementScrap; // 공고 스크랩 여부
        private int announcementScrapCount; // 공고의 전체 스크랩 개수
    }

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class AddTeamMemberAnnouncementResponse {
        private Long teamMemberAnnouncementId;
        private String announcementTitle;

        @Builder.Default
        private AnnouncementPositionItem announcementPositionItem = new AnnouncementPositionItem();

        @Builder.Default
        private List<TeamMemberAnnouncementResponseDTO.AnnouncementSkillName>
                announcementSkillNames = new ArrayList<>();

        private String announcementEndDate;

        private Boolean isPermanentRecruitment;

        private Boolean isRegionFlexible; // 지역 무관

        private String mainTasks; // 주요 업무
        private String workMethod; // 업무 방식
        private String idealCandidate; // 이런 분을 찾고 있어요

        // 필수 기입 아님
        private String preferredQualifications; // 이런 분이면 더 좋아요
        private String joiningProcess; // 이런 과정으로 합류해요
        private String benefits; // 합류하면 이런 것들을 얻어 갈 수 있어요
    }

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UpdateTeamMemberAnnouncementResponse {
        private Long teamMemberAnnouncementId;
        private String announcementTitle;

        @Builder.Default
        private AnnouncementPositionItem announcementPositionItem = new AnnouncementPositionItem();

        @Builder.Default
        private List<TeamMemberAnnouncementResponseDTO.AnnouncementSkillName>
                announcementSkillNames = new ArrayList<>();

        private String announcementEndDate;
        private Boolean isPermanentRecruitment;

        private Boolean isRegionFlexible; // 지역 무관

        private String mainTasks; // 주요 업무
        private String workMethod; // 업무 방식
        private String idealCandidate; // 이런 분을 찾고 있어요

        // 필수 기입 아님
        private String preferredQualifications; // 이런 분이면 더 좋아요
        private String joiningProcess; // 이런 과정으로 합류해요
        private String benefits; // 합류하면 이런 것들을 얻어 갈 수 있어요
    }

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class RemoveTeamMemberAnnouncementResponse {
        private Long teamMemberAnnouncementId;
    }

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class AnnouncementPositionItem {
        private String majorPosition;
        private String subPosition;
    }

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class AnnouncementSkillName {
        private String announcementSkillName;
    }

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UpdateTeamMemberAnnouncementPublicStateResponse {
        private Long teamMemberAnnouncementId;
        private Boolean isAnnouncementPublic;
    }
}
