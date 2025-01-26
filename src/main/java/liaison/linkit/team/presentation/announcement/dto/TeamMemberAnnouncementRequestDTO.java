package liaison.linkit.team.presentation.announcement.dto;

import jakarta.validation.constraints.Pattern;
import java.util.ArrayList;
import java.util.List;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
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

        @NotBlank(message = "공고 제목은 필수입니다")
        @Size(min = 1, message = "공고 제목은 1자 이상이어야 합니다")
        private String announcementTitle;

        @NotBlank(message = "대분류 포지션은 필수입니다")
        @Size(min = 1, message = "대분류 포지션은 1자 이상이어야 합니다")
        private String majorPosition;

        @NotBlank(message = "소분류 포지션은 필수입니다")
        @Size(min = 1, message = "소분류 포지션은 1자 이상이어야 합니다")
        private String subPosition;

        @Builder.Default
        private List<AnnouncementSkillName> announcementSkillNames = new ArrayList<>();

        @NotBlank(message = "공고 종료일은 필수입니다")
        private String announcementEndDate;

        @NotNull(message = "지역 무관 여부는 필수입니다")
        private Boolean isRegionFlexible;

        @NotBlank(message = "주요 업무는 필수입니다")
        @Size(min = 1, message = "주요 업무는 1자 이상이어야 합니다")
        private String mainTasks;

        @NotBlank(message = "업무 방식은 필수입니다")
        @Size(min = 1, message = "업무 방식은 1자 이상이어야 합니다")
        private String workMethod;

        @NotBlank(message = "이상적인 후보자 설명은 필수입니다")
        @Size(min = 1, message = "이상적인 후보자 설명은 1자 이상이어야 합니다")
        private String idealCandidate;

        private String preferredQualifications;
        private String joiningProcess;
        private String benefits;
    }

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UpdateTeamMemberAnnouncementRequest {
        @NotBlank(message = "공고 제목은 필수입니다")
        @Size(min = 1, message = "공고 제목은 1자 이상이어야 합니다")
        private String announcementTitle;

        @NotBlank(message = "대분류 포지션은 필수입니다")
        @Size(min = 1, message = "대분류 포지션은 1자 이상이어야 합니다")
        private String majorPosition;

        @NotBlank(message = "소분류 포지션은 필수입니다")
        @Size(min = 1, message = "소분류 포지션은 1자 이상이어야 합니다")
        private String subPosition;

        @Builder.Default
        private List<AnnouncementSkillName> announcementSkillNames = new ArrayList<>();

        @NotBlank(message = "공고 종료일은 필수입니다")
        @Pattern(regexp = "^\\d{4}\\.(0[1-9]|1[0-2])$",
                message = "날짜 형식이 올바르지 않습니다. (YYYY.MM)")
        private String announcementEndDate;

        @NotNull(message = "지역 무관 여부는 필수입니다")
        private Boolean isRegionFlexible;

        @NotBlank(message = "주요 업무는 필수입니다")
        @Size(min = 1, message = "주요 업무는 1자 이상이어야 합니다")
        private String mainTasks;

        @NotBlank(message = "업무 방식은 필수입니다")
        @Size(min = 1, message = "업무 방식은 1자 이상이어야 합니다")
        private String workMethod;

        @NotBlank(message = "이런 분을 찾고 있어요는 필수입니다")
        @Size(min = 1, message = "이런 분을 찾고 있어요는 1자 이상이어야 합니다")
        private String idealCandidate;

        private String preferredQualifications;
        private String joiningProcess;
        private String benefits;
    }

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class AnnouncementSkillName {
        @NotBlank(message = "기술 스택 이름은 필수입니다")
        @Size(min = 1, message = "기술 스택 이름은 1자 이상이어야 합니다")
        private String announcementSkillName;
    }
}
