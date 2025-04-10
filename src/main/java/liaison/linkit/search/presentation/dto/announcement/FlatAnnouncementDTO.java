package liaison.linkit.search.presentation.dto.announcement;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class FlatAnnouncementDTO {
    private Long teamMemberAnnouncementId;

    private String teamLogoImagePath;
    private String teamName;
    private String teamCode;

    private String teamScaleName;

    private String cityName;
    private String divisionName;

    private Boolean isAnnouncementInProgress;
    private String announcementEndDate;
    private Boolean isPermanentRecruitment; // 상시 모집 여부
    private String announcementTitle; // 공고 제목

    private Long viewCount;

    private String majorPosition;
    private String subPosition;

    private String announcementSkillName;

    private LocalDateTime createdAt;
}
