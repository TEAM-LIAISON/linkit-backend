package liaison.linkit.search.service;

import java.util.List;
import liaison.linkit.common.business.RegionMapper;
import liaison.linkit.common.implement.RegionQueryAdapter;
import liaison.linkit.common.presentation.RegionResponseDTO.RegionDetail;
import liaison.linkit.team.business.TeamCurrentStateMapper;
import liaison.linkit.team.business.TeamMapper;
import liaison.linkit.team.business.TeamScaleMapper;
import liaison.linkit.team.domain.Team;
import liaison.linkit.team.domain.TeamCurrentState;
import liaison.linkit.team.domain.TeamRegion;
import liaison.linkit.team.domain.scale.TeamScale;
import liaison.linkit.team.implement.TeamQueryAdapter;
import liaison.linkit.team.implement.scale.TeamScaleQueryAdapter;
import liaison.linkit.team.presentation.team.dto.TeamResponseDTO.TeamCurrentStateItem;
import liaison.linkit.team.presentation.team.dto.TeamResponseDTO.TeamInformMenu;
import liaison.linkit.team.presentation.team.dto.TeamResponseDTO.TeamScaleItem;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class TeamSearchService {

    private final TeamQueryAdapter teamQueryAdapter;
    private final RegionQueryAdapter regionQueryAdapter;

    private final TeamScaleQueryAdapter teamScaleQueryAdapter;

    private final RegionMapper regionMapper;
    private final TeamMapper teamMapper;
    private final TeamCurrentStateMapper teamCurrentStateMapper;
    private final TeamScaleMapper teamScaleMapper;

    public Page<TeamInformMenu> searchTeams(
            List<String> scaleName,
            Boolean isAnnouncement,
            List<String> cityName,
            List<String> teamStateName,
            Pageable pageable
    ) {
        log.info("검색 조건 - ScaleName: {}, isAnnouncement: {}, CityName: {}, TeamStateName: {}",
                scaleName, isAnnouncement, cityName, teamStateName);
        Page<Team> teams = teamQueryAdapter.findAllByFiltering(scaleName, isAnnouncement, cityName, teamStateName, pageable);
        log.info("검색 결과 - 총 팀 수: {}", teams.getTotalElements());

        return teams.map(this::toSearchTeamInformMenu);
    }

    private TeamInformMenu toSearchTeamInformMenu(
            final Team team
    ) {
        RegionDetail regionDetail = new RegionDetail();
        if (regionQueryAdapter.existsTeamRegionByTeamId((team.getId()))) {
            final TeamRegion teamRegion = regionQueryAdapter.findTeamRegionByTeamId(team.getId());
            regionDetail = regionMapper.toRegionDetail(teamRegion.getRegion());
        }
        log.info("지역 정보 조회 성공");

        final List<TeamCurrentState> teamCurrentStates = teamQueryAdapter.findTeamCurrentStatesByTeamId(team.getId());
        final List<TeamCurrentStateItem> teamCurrentStateItems = teamCurrentStateMapper.toTeamCurrentStateItems(teamCurrentStates);
        log.info("팀 상태 정보 조회 성공");

        final TeamScale teamScale = teamScaleQueryAdapter.findTeamScaleByTeamId(team.getId());
        final TeamScaleItem teamScaleItem = teamScaleMapper.toTeamScaleItem(teamScale);

        return teamMapper.toTeamInformMenu(team, teamCurrentStateItems, teamScaleItem, regionDetail);
    }
}
