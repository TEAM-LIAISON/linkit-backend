package liaison.linkit.search.presentation.dto;

import java.util.ArrayList;
import java.util.List;
import liaison.linkit.team.presentation.team.dto.TeamResponseDTO.TeamInformMenu;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.Page;

@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@AllArgsConstructor
public class TeamSearchResponseDTO {

    /**
     * 상단 영역: 창업을 위한 팀원을 찾고 있어요 4개의 팀 리스트
     */
    @Builder.Default
    private List<TeamInformMenu> ventureTeams = new ArrayList<>();

    /**
     * 중간 영역: 지원사업을 준비 중인 팀이에요 4개의 팀 리스트
     */
    @Builder.Default
    private List<TeamInformMenu> supportProjectTeams = new ArrayList<>();

    /**
     * 하단 영역: 상단에 포함된 팀을 제외한 나머지 팀들의 페이지네이션 결과
     */
    private Page<TeamInformMenu> defaultTeams;
}
