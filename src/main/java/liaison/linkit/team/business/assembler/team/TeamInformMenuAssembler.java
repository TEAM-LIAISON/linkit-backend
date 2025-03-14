package liaison.linkit.team.business.assembler.team;

import java.util.List;
import java.util.Optional;

import liaison.linkit.common.business.RegionMapper;
import liaison.linkit.common.implement.RegionQueryAdapter;
import liaison.linkit.common.presentation.RegionResponseDTO.RegionDetail;
import liaison.linkit.profile.implement.profile.ProfileQueryAdapter;
import liaison.linkit.scrap.implement.teamScrap.TeamScrapQueryAdapter;
import liaison.linkit.team.business.mapper.scale.TeamScaleMapper;
import liaison.linkit.team.business.mapper.state.TeamCurrentStateMapper;
import liaison.linkit.team.business.mapper.team.TeamMapper;
import liaison.linkit.team.domain.region.TeamRegion;
import liaison.linkit.team.domain.scale.TeamScale;
import liaison.linkit.team.domain.state.TeamCurrentState;
import liaison.linkit.team.domain.team.Team;
import liaison.linkit.team.implement.scale.TeamScaleQueryAdapter;
import liaison.linkit.team.implement.team.TeamQueryAdapter;
import liaison.linkit.team.presentation.team.dto.TeamResponseDTO.TeamCurrentStateItem;
import liaison.linkit.team.presentation.team.dto.TeamResponseDTO.TeamInformMenu;
import liaison.linkit.team.presentation.team.dto.TeamResponseDTO.TeamScaleItem;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class TeamInformMenuAssembler {

    // Adapter
    private final RegionQueryAdapter regionQueryAdapter;
    private final TeamQueryAdapter teamQueryAdapter;
    private final TeamScrapQueryAdapter teamScrapQueryAdapter;
    private final TeamScaleQueryAdapter teamScaleQueryAdapter;
    private final ProfileQueryAdapter profileQueryAdapter;

    // Mapper
    private final RegionMapper regionMapper;
    private final TeamScaleMapper teamScaleMapper;
    private final TeamCurrentStateMapper teamCurrentStateMapper;
    private final TeamMapper teamMapper;

    // Event Publisher
    private final ApplicationEventPublisher applicationEventPublisher;

    /**
     * 1. 지역 정보 조회 메서드
     *
     * @param targetTeam 조회 대상 팀
     * @return 조회된 RegionDetail, 없으면 기본 인스턴스 반환
     */
    public RegionDetail assembleRegionDetail(final Team targetTeam) {
        RegionDetail regionDetail = new RegionDetail();
        if (regionQueryAdapter.existsTeamRegionByTeamId((targetTeam.getId()))) {
            final TeamRegion teamRegion =
                    regionQueryAdapter.findTeamRegionByTeamId(targetTeam.getId());
            regionDetail = regionMapper.toRegionDetail(teamRegion.getRegion());
        }

        return regionDetail;
    }

    /**
     * 2. 팀 규모 정보 조회 메서드
     *
     * @param targetTeam 조회 대상 팀
     * @return 조회된 TeamScaleItem, 없으면 기본 인스턴스 반환
     */
    public TeamScaleItem assembleTeamScaleItem(final Team targetTeam) {
        TeamScaleItem teamScaleItem = new TeamScaleItem();
        if (teamScaleQueryAdapter.existsTeamScaleByTeamId(targetTeam.getId())) {
            final TeamScale teamScale =
                    teamScaleQueryAdapter.findTeamScaleByTeamId(targetTeam.getId());
            teamScaleItem = teamScaleMapper.toTeamScaleItem(teamScale);
        }
        return teamScaleItem;
    }

    /**
     * 3. 팀 현재 상태 정보 조회 메서드
     *
     * @param targetTeam 조회 대상 팀
     * @return 조회된 TeamCurrentStateItem, 없으면 기본 인스턴스 반환
     */
    public List<TeamCurrentStateItem> assembleTeamCurrentStateItems(final Team targetTeam) {
        final List<TeamCurrentState> teamCurrentStates =
                teamQueryAdapter.findTeamCurrentStatesByTeamId(targetTeam.getId());
        return teamCurrentStateMapper.toTeamCurrentStateItems(teamCurrentStates);
    }

    /**
     * 4. 스크랩 여부 조회 메서드 (로그인 상태인 경우)
     *
     * @param targetTeam 조회 대상 팀
     * @param loggedInMemberId 로그인한 사용자의 memberId(Optional)
     * @return 스크랩 여부, 로그인 상태가 아니면 기본 false 반환
     */
    public boolean assembleIsTeamScrap(
            final Team targetTeam, final Optional<Long> loggedInMemberId) {
        boolean isTeamScrap = false;
        if (loggedInMemberId.isPresent()) {
            isTeamScrap =
                    teamScrapQueryAdapter.existsByMemberIdAndTeamCode(
                            loggedInMemberId.get(), targetTeam.getTeamCode());
        }
        return isTeamScrap;
    }

    /**
     * 5. 스크랩 수 조회 메서드
     *
     * @param targetTeam 조회 대상 팀
     * @return 해당 팀의 총 스크랩 수
     */
    public int assembleTeamScrapCount(final Team targetTeam) {
        return teamScrapQueryAdapter.countTotalTeamScrapByTeamCode(targetTeam.getTeamCode());
    }

    /**
     * 7. 최종 TeamInformMenu DTO 조립 메서드
     *
     * @param targetTeam 조회 대상 팀
     * @param loggedInMemberId Optional 로그인한 사용자의 memberId. 값이 존재하면 로그인 상태, 없으면 로그아웃 상태로 처리
     * @return 최종 조립된 TeamInformMenu DTO
     */
    public TeamInformMenu assembleTeamInformMenu(
            final Team targetTeam, final Optional<Long> loggedInMemberId) {
        final boolean isTeamScrap = assembleIsTeamScrap(targetTeam, loggedInMemberId);
        final int teamScrapCount = assembleTeamScrapCount(targetTeam);
        final List<TeamCurrentStateItem> teamCurrentStateItems =
                assembleTeamCurrentStateItems(targetTeam);
        final TeamScaleItem teamScaleItem = assembleTeamScaleItem(targetTeam);
        final RegionDetail regionDetail = assembleRegionDetail(targetTeam);

        return teamMapper.toTeamInformMenu(
                targetTeam,
                isTeamScrap,
                teamScrapCount,
                teamCurrentStateItems,
                teamScaleItem,
                regionDetail);
    }
}
