package liaison.linkit.search.presentation.dto.announcement;

import liaison.linkit.search.presentation.dto.cursor.CursorResponse;
import liaison.linkit.team.presentation.announcement.dto.TeamMemberAnnouncementResponseDTO.AnnouncementInformMenu;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AnnouncementSearchResponseDTO {

    // 기본 팀 목록 (커서 기반 페이지네이션 적용)
    private CursorResponse<AnnouncementInformMenu> defaultAnnouncements;

    /** 일반 검색 결과를 위한 팩토리 메서드 */
    public static AnnouncementSearchResponseDTO ofFiltered(
            CursorResponse<AnnouncementInformMenu> defaultAnnouncements) {
        return AnnouncementSearchResponseDTO.builder()
                .defaultAnnouncements(defaultAnnouncements)
                .build();
    }

    /** 기본 검색 결과(벤처 팀과 지원 프로젝트 팀 포함)를 위한 팩토리 메서드 */
    public static AnnouncementSearchResponseDTO ofDefault(
            CursorResponse<AnnouncementInformMenu> defaultAnnouncements) {
        return AnnouncementSearchResponseDTO.builder()
                .defaultAnnouncements(defaultAnnouncements)
                .build();
    }
}
