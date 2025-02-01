package liaison.linkit.search.business.service;

import java.util.List;
import liaison.linkit.common.business.RegionMapper;
import liaison.linkit.common.implement.RegionQueryAdapter;
import liaison.linkit.common.presentation.RegionResponseDTO.RegionDetail;
import liaison.linkit.scrap.implement.teamScrap.TeamScrapQueryAdapter;
import liaison.linkit.team.business.mapper.state.TeamCurrentStateMapper;
import liaison.linkit.team.business.mapper.team.TeamMapper;
import liaison.linkit.team.business.mapper.scale.TeamScaleMapper;
import liaison.linkit.team.domain.team.Team;
import liaison.linkit.team.domain.state.TeamCurrentState;
import liaison.linkit.team.domain.region.TeamRegion;
import liaison.linkit.team.domain.scale.TeamScale;
import liaison.linkit.team.implement.team.TeamQueryAdapter;
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
    private final TeamScrapQueryAdapter teamScrapQueryAdapter;

    public Page<TeamInformMenu> searchTeamsInLogoutState(
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

        return teams.map(this::toSearchTeamInformMenuInLogoutState);
    }

    public Page<TeamInformMenu> searchTeamsInLoginState(
            final Long memberId,
            List<String> scaleName,
            Boolean isAnnouncement,
            List<String> cityName,
            List<String> teamStateName,
            Pageable pageable
    ) {
        Page<Team> teams = teamQueryAdapter.findAllByFiltering(scaleName, isAnnouncement, cityName, teamStateName, pageable);
        return teams.map(team -> toSearchTeamInformMenuInLogInState(team, memberId));
    }


    private TeamInformMenu toSearchTeamInformMenuInLogoutState(
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

        final int teamScrapCount = teamScrapQueryAdapter.countTotalTeamScrapByTeamCode(team.getTeamCode());

        return teamMapper.toTeamInformMenu(team, false, teamScrapCount, teamCurrentStateItems, teamScaleItem, regionDetail);
    }

    private TeamInformMenu toSearchTeamInformMenuInLogInState(
            final Team team, final Long memberId
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

        final boolean isTeamScrap = teamScrapQueryAdapter.existsByMemberIdAndTeamCode(memberId, team.getTeamCode());

        final int teamScrapCount = teamScrapQueryAdapter.countTotalTeamScrapByTeamCode(team.getTeamCode());

        return teamMapper.toTeamInformMenu(team, isTeamScrap, teamScrapCount, teamCurrentStateItems, teamScaleItem, regionDetail);
    }
}
