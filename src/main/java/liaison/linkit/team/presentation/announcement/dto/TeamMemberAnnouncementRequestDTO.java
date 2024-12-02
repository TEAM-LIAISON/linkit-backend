package liaison.linkit.team.presentation.announcement.dto;

import java.util.ArrayList;
import java.util.List;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class TeamMemberAnnouncementRequestDTO {

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class AddTeamMemberAnnouncementRequest {
        private String announcementTitle;

        private String majorPosition; // 대분류
        private String subPosition; // 소분류

        @Builder.Default
        private List<AnnouncementSkillName> announcementSkillNames = new ArrayList<>();

        private String announcementStartDate;
        private String announcementEndDate;

        private String cityName;    // 시/도
        private String divisionName; // 시/군/구
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
    public static class UpdateTeamMemberAnnouncementRequest {
        private String announcementTitle;

        private String majorPosition;
        private String subPosition;

        @Builder.Default
        private List<TeamMemberAnnouncementRequestDTO.AnnouncementSkillName> announcementSkillNames = new ArrayList<>();

        private String announcementStartDate;
        private String announcementEndDate;

        private String cityName;
        private String divisionName;
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
    public static class AnnouncementSkillName {
        private String announcementSkillName;
    }


}
