package liaison.linkit.search.presentation.dto.team;

import liaison.linkit.search.presentation.dto.cursor.CursorResponse;
import liaison.linkit.team.presentation.team.dto.TeamResponseDTO.TeamInformMenu;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TeamSearchResponseDTO {

    // 기본 팀 목록 (커서 기반 페이지네이션 적용)
    private CursorResponse<TeamInformMenu> defaultTeams;

    /** 일반 검색 결과를 위한 팩토리 메서드 */
    public static TeamSearchResponseDTO ofFiltered(CursorResponse<TeamInformMenu> defaultTeams) {
        return TeamSearchResponseDTO.builder().defaultTeams(defaultTeams).build();
    }

    /** 기본 검색 결과(벤처 팀과 지원 프로젝트 팀 포함)를 위한 팩토리 메서드 */
    public static TeamSearchResponseDTO ofDefault(CursorResponse<TeamInformMenu> defaultTeams) {
        return TeamSearchResponseDTO.builder().defaultTeams(defaultTeams).build();
    }
}
