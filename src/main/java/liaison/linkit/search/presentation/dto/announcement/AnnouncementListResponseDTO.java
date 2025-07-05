package liaison.linkit.search.presentation.dto.announcement;

import java.util.ArrayList;
import java.util.List;

import liaison.linkit.team.presentation.announcement.dto.TeamMemberAnnouncementResponseDTO;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/** 주요 공고 목록 조회 응답 DTO - 지금 핫한 공고만 포함합니다. */
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class AnnouncementListResponseDTO {

    /** 상단 영역: 지금 핫한 공고에요! 6개의 공고 리스트 */
    @Builder.Default
    private List<TeamMemberAnnouncementResponseDTO.AnnouncementInformMenu> hotAnnouncements =
            new ArrayList<>();

    /** 벤처 팀과 지원 프로젝트 팀을 포함한 DTO 생성 */
    public static AnnouncementListResponseDTO of(
            List<TeamMemberAnnouncementResponseDTO.AnnouncementInformMenu> hotAnnouncements) {
        return AnnouncementListResponseDTO.builder().hotAnnouncements(hotAnnouncements).build();
    }
}
