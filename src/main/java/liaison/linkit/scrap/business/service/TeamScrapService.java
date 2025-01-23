package liaison.linkit.scrap.business.service;

import java.util.ArrayList;
import java.util.List;
import liaison.linkit.common.business.RegionMapper;
import liaison.linkit.common.implement.RegionQueryAdapter;
import liaison.linkit.common.presentation.RegionResponseDTO.RegionDetail;
import liaison.linkit.member.domain.Member;
import liaison.linkit.member.implement.MemberQueryAdapter;
import liaison.linkit.scrap.business.mapper.TeamScrapMapper;
import liaison.linkit.scrap.domain.TeamScrap;
import liaison.linkit.scrap.exception.teamScrap.TeamScrapBadRequestException;
import liaison.linkit.scrap.implement.teamScrap.TeamScrapCommandAdapter;
import liaison.linkit.scrap.implement.teamScrap.TeamScrapQueryAdapter;
import liaison.linkit.scrap.presentation.dto.teamScrap.TeamScrapRequestDTO.UpdateTeamScrapRequest;
import liaison.linkit.scrap.presentation.dto.teamScrap.TeamScrapResponseDTO;
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
import liaison.linkit.team.presentation.team.dto.TeamResponseDTO.TeamInformMenus;
import liaison.linkit.team.presentation.team.dto.TeamResponseDTO.TeamScaleItem;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class TeamScrapService {
    private final MemberQueryAdapter memberQueryAdapter;
    private final TeamScrapQueryAdapter teamScrapQueryAdapter;
    private final TeamQueryAdapter teamQueryAdapter;
//    private final ScrapValidator scrapValidator;

    private final TeamScrapCommandAdapter teamScrapCommandAdapter;

    private final TeamScrapMapper teamScrapMapper;
    private final TeamCurrentStateMapper teamCurrentStateMapper;
    private final RegionQueryAdapter regionQueryAdapter;
    private final RegionMapper regionMapper;
    private final TeamScaleQueryAdapter teamScaleQueryAdapter;
    private final TeamScaleMapper teamScaleMapper;
    private final TeamMapper teamMapper;

    // 회원이 팀 스크랩 버튼을 눌렀을 떄의 메서드
    public TeamScrapResponseDTO.UpdateTeamScrap updateTeamScrap(
            final Long memberId,
            final String teamCode,
            final UpdateTeamScrapRequest updateTeamScrapRequest
    ) {

        boolean shouldAddScrap = updateTeamScrapRequest.isChangeScrapValue();

//        scrapValidator.validateSelfTeamScrap(memberId, teamCode); // 자기 자신이 속한 팀 스크랩에 대한 예외 처리
//        scrapValidator.validateMemberMaxTeamScrap(memberId);         // 최대 프로필 스크랩 개수에 대한 예외 처리

        boolean scrapExists = teamScrapQueryAdapter.existsByMemberIdAndTeamCode(memberId, teamCode);

        if (scrapExists) {
            handleExistingScrap(memberId, teamCode, shouldAddScrap);
            log.info("Team scrap updated for member " + memberId);
        } else {
            log.info("Team scrap does not exist");
            handleNonExistingScrap(memberId, teamCode, shouldAddScrap);
        }

        log.info("Team scrap updated for member " + memberId);
        return teamScrapMapper.toUpdateTeamScrap(teamCode, shouldAddScrap);
    }

    @Transactional(readOnly = true)
    public TeamInformMenus getTeamScraps(final Long memberId) {
        // 1) memberId로 TeamScrap 목록 조회
        final List<TeamScrap> teamScraps = teamScrapQueryAdapter.findAllByMemberId(memberId);

        // 2) TeamScrap -> Team 리스트 추출
        final List<Team> teams = teamScraps.stream()
                .map(TeamScrap::getTeam)
                .toList();

        // 3) 각 팀에 대해 필요한 정보를 조회한 뒤, TeamInformMenu 형태로 매핑
        final List<TeamInformMenu> teamInformMenus = new ArrayList<>();

        for (Team team : teams) {
            RegionDetail regionDetail = new RegionDetail();
            if (regionQueryAdapter.existsTeamRegionByTeamId((team.getId()))) {
                final TeamRegion teamRegion = regionQueryAdapter.findTeamRegionByTeamId(team.getId());
                regionDetail = regionMapper.toRegionDetail(teamRegion.getRegion());
            }
            log.info("팀 지역 정보 조회 성공");

            final List<TeamCurrentState> teamCurrentStates = teamQueryAdapter.findTeamCurrentStatesByTeamId(team.getId());
            final List<TeamCurrentStateItem> teamCurrentStateItems = teamCurrentStateMapper.toTeamCurrentStateItems(teamCurrentStates);
            log.info("팀 상태 정보 조회 성공");

            final TeamScale teamScale = teamScaleQueryAdapter.findTeamScaleByTeamId(team.getId());
            final TeamScaleItem teamScaleItem = teamScaleMapper.toTeamScaleItem(teamScale);

            final boolean isTeamScrap = teamScrapQueryAdapter.existsByMemberIdAndTeamCode(memberId, team.getTeamCode());
            final int teamScrapCount = teamScrapQueryAdapter.countTotalTeamScrapByTeamCode(team.getTeamCode());

            final TeamInformMenu teamInformMenu = teamMapper.toTeamInformMenu(team, isTeamScrap, teamScrapCount, teamCurrentStateItems, teamScaleItem, regionDetail);
            teamInformMenus.add(teamInformMenu);
        }

        return teamScrapMapper.toTeamInformMenus(teamInformMenus);
    }

    // 스크랩이 존재하는 경우 처리 메서드
    private void handleExistingScrap(Long memberId, String teamCode, boolean shouldAddScrap) {
        if (!shouldAddScrap) {
            teamScrapCommandAdapter.deleteByMemberIdAndTeamCode(memberId, teamCode);
        } else {
            throw TeamScrapBadRequestException.EXCEPTION;
        }
    }

    // 스크랩이 존재하지 않는 경우 처리 메서드
    private void handleNonExistingScrap(Long memberId, String teamCode, boolean shouldAddScrap) {
        log.info("handleNonExistingScrap 실행");
        if (shouldAddScrap) {
            log.info("shouldAddScrap true");
            Member member = memberQueryAdapter.findById(memberId);
            log.info("memberId : " + memberId);
            Team team = teamQueryAdapter.findByTeamCode(teamCode);
            log.info("teamCode : " + teamCode);
            TeamScrap teamScrap = new TeamScrap(null, member, team);
            log.info("teamScrap : " + teamScrap);
            teamScrapCommandAdapter.addTeamScrap(teamScrap);
        } else {
            log.info("shouldAddScrap false");
            throw TeamScrapBadRequestException.EXCEPTION;
        }
    }

}
