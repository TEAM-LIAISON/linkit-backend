package liaison.linkit.search.presentation.dto.team;

import java.util.List;

import liaison.linkit.team.presentation.team.dto.TeamResponseDTO.TeamInformMenu;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/** 주요 팀 목록 조회 응답 DTO - 벤처 팀과 지원 프로젝트 팀만 포함합니다. */
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TeamListResponseDTO {
    // 벤처 팀 목록
    private List<TeamInformMenu> ventureTeams;

    /** 벤처 팀과 지원 프로젝트 팀을 포함한 DTO 생성 */
    public static TeamListResponseDTO of(List<TeamInformMenu> ventureTeams) {
        return TeamListResponseDTO.builder().ventureTeams(ventureTeams).build();
    }
}
