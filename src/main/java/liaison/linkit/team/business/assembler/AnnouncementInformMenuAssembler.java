package liaison.linkit.team.business.assembler;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import liaison.linkit.common.business.RegionMapper;
import liaison.linkit.common.implement.RegionQueryAdapter;
import liaison.linkit.common.presentation.RegionResponseDTO.RegionDetail;
import liaison.linkit.global.util.DateUtils;
import liaison.linkit.scrap.implement.announcementScrap.AnnouncementScrapQueryAdapter;
import liaison.linkit.team.business.mapper.announcement.AnnouncementSkillMapper;
import liaison.linkit.team.business.mapper.announcement.TeamMemberAnnouncementMapper;
import liaison.linkit.team.business.mapper.scale.TeamScaleMapper;
import liaison.linkit.team.domain.announcement.AnnouncementPosition;
import liaison.linkit.team.domain.announcement.AnnouncementSkill;
import liaison.linkit.team.domain.announcement.TeamMemberAnnouncement;
import liaison.linkit.team.domain.region.TeamRegion;
import liaison.linkit.team.domain.scale.TeamScale;
import liaison.linkit.team.domain.team.Team;
import liaison.linkit.team.implement.announcement.AnnouncementPositionQueryAdapter;
import liaison.linkit.team.implement.announcement.AnnouncementSkillQueryAdapter;
import liaison.linkit.team.implement.announcement.TeamMemberAnnouncementQueryAdapter;
import liaison.linkit.team.implement.scale.TeamScaleQueryAdapter;
import liaison.linkit.team.presentation.announcement.dto.TeamMemberAnnouncementResponseDTO.AnnouncementInformMenu;
import liaison.linkit.team.presentation.announcement.dto.TeamMemberAnnouncementResponseDTO.AnnouncementInformMenus;
import liaison.linkit.team.presentation.announcement.dto.TeamMemberAnnouncementResponseDTO.AnnouncementPositionItem;
import liaison.linkit.team.presentation.announcement.dto.TeamMemberAnnouncementResponseDTO.AnnouncementSkillName;
import liaison.linkit.team.presentation.team.dto.TeamResponseDTO.TeamScaleItem;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class AnnouncementInformMenuAssembler {

    // Adapters
    private final TeamMemberAnnouncementQueryAdapter teamMemberAnnouncementQueryAdapter;
    private final AnnouncementSkillQueryAdapter announcementSkillQueryAdapter;
    private final AnnouncementScrapQueryAdapter announcementScrapQueryAdapter;
    private final RegionQueryAdapter regionQueryAdapter;

    // Mappers
    private final RegionMapper regionMapper;
    private final TeamMemberAnnouncementMapper teamMemberAnnouncementMapper;
    private final AnnouncementSkillMapper announcementSkillMapper;
    private final TeamScaleQueryAdapter teamScaleQueryAdapter;
    private final TeamScaleMapper teamScaleMapper;
    private final AnnouncementPositionQueryAdapter announcementPositionQueryAdapter;

    /**
     * 홈 화면에 표시할 공고 목록(AnnouncementInformMenus)을 조립하여 반환합니다. 로그인 상태(Optional memberId 값 존재)와 로그아웃 상태(Optional.empty()) 모두 처리합니다.
     *
     * @param optionalMemberId 로그인한 회원의 ID(Optional). 값이 있으면 로그인 상태, 없으면 로그아웃 상태로 처리합니다.
     * @return 조립된 AnnouncementInformMenus DTO.
     */
    public AnnouncementInformMenus assembleHomeAnnouncementInformMenus(final Optional<Long> optionalMemberId) {
        // 최대 9개의 공고 조회 (예시)
        List<TeamMemberAnnouncement> announcements =
            teamMemberAnnouncementQueryAdapter.findTopTeamMemberAnnouncements(9);

        // 각 공고를 AnnouncementInformMenu DTO로 매핑
        List<AnnouncementInformMenu> announcementInformMenus = announcements.stream()
            .map(announcement -> mapToAnnouncementInformMenu(announcement, optionalMemberId))
            .toList();

        // 최종 목록 DTO로 변환하여 반환
        return teamMemberAnnouncementMapper.toAnnouncementInformMenus(announcementInformMenus);
    }

    /**
     * 공고(TeamMemberAnnouncement)를 AnnouncementInformMenu DTO로 변환합니다. 로그인 상태(Optional 값 존재)와 로그아웃 상태를 모두 처리할 수 있습니다.
     *
     * @param teamMemberAnnouncement 조회할 공고 엔티티.
     * @param optionalMemberId       로그인한 회원의 ID(Optional). 값이 있으면 스크랩 여부를 조회합니다.
     * @return AnnouncementInformMenu DTO.
     */
    public AnnouncementInformMenu mapToAnnouncementInformMenu(
        final TeamMemberAnnouncement teamMemberAnnouncement,
        final Optional<Long> optionalMemberId
    ) {
        // 1. 팀 정보 조회
        final Team team = teamMemberAnnouncement.getTeam();

        // 2. 공고 스킬 정보 조회
        List<AnnouncementSkillName> announcementSkillNames = fetchAnnouncementSkills(teamMemberAnnouncement);

        // 3. D-Day 계산 (상시 모집이 아니며 종료 날짜가 있을 때 계산)
        int announcementDDay = -1;
        if (!teamMemberAnnouncement.isPermanentRecruitment() && teamMemberAnnouncement.getAnnouncementEndDate() != null) {
            announcementDDay = DateUtils.calculateDDay(teamMemberAnnouncement.getAnnouncementEndDate());
        }

        // 4. 공고 스크랩 여부 (로그인 상태이면 조회, 아니면 false)
        boolean isAnnouncementScrap = optionalMemberId
            .map(memberId -> announcementScrapQueryAdapter.existsByMemberIdAndTeamMemberAnnouncementId(memberId, teamMemberAnnouncement.getId()))
            .orElse(false);

        // 5. 공고 스크랩 수 조회 (로그인 여부와 무관하게 조회)
        int announcementScrapCount = announcementScrapQueryAdapter.getTotalAnnouncementScrapCount(teamMemberAnnouncement.getId());

        // 6. 추가 정보 조회: 팀 규모, 팀 지역, 공고 포지션 정보
        TeamScaleItem teamScaleItem = fetchTeamScaleItem(team);
        RegionDetail regionDetail = fetchRegionDetail(team);
        AnnouncementPositionItem announcementPositionItem = fetchAnnouncementPositionItem(teamMemberAnnouncement);

        // 7. 최종 DTO 변환 및 반환
        return teamMemberAnnouncementMapper.toTeamMemberAnnouncementInform(
            team.getTeamLogoImagePath(),
            team.getTeamName(),
            team.getTeamCode(),
            teamScaleItem,
            regionDetail,
            teamMemberAnnouncement,
            announcementDDay,
            isAnnouncementScrap,
            announcementScrapCount,
            announcementPositionItem,
            announcementSkillNames
        );
    }

    // ─────────────────────────────────────────────────────────────
    // 헬퍼 메서드들
    // ─────────────────────────────────────────────────────────────

    /**
     * 팀 규모 정보를 조회하여 TeamScaleItem으로 반환합니다. 실제 구현에서는 teamScaleQueryAdapter와 teamScaleMapper를 사용합니다.
     *
     * @param team 조회 대상 팀.
     * @return 팀 규모 정보 DTO, 정보가 없으면 null 반환.
     */
    private TeamScaleItem fetchTeamScaleItem(final Team team) {
        if (teamScaleQueryAdapter.existsTeamScaleByTeamId(team.getId())) {
            TeamScale teamScale = teamScaleQueryAdapter.findTeamScaleByTeamId(team.getId());
            return teamScaleMapper.toTeamScaleItem(teamScale);
        }
        return new TeamScaleItem();
    }


    /**
     * 팀 지역 정보를 조회하여 RegionDetail로 반환합니다.
     *
     * @param team 조회 대상 팀.
     * @return 조회된 RegionDetail. 정보가 없으면 기본 RegionDetail 인스턴스 반환.
     */
    private RegionDetail fetchRegionDetail(final Team team) {
        if (regionQueryAdapter.existsTeamRegionByTeamId(team.getId())) {
            TeamRegion teamRegion = regionQueryAdapter.findTeamRegionByTeamId(team.getId());
            return regionMapper.toRegionDetail(teamRegion.getRegion());
        }
        return new RegionDetail();
    }

    /**
     * 공고 포지션 정보를 조회하여 AnnouncementPositionItem으로 반환합니다.
     *
     * @param teamMemberAnnouncement 조회 대상 공고 엔티티.
     * @return AnnouncementPositionItem DTO, 정보가 없으면 기본 인스턴스 반환.
     */
    private AnnouncementPositionItem fetchAnnouncementPositionItem(final TeamMemberAnnouncement teamMemberAnnouncement) {
        if (announcementPositionQueryAdapter.existsAnnouncementPositionByTeamMemberAnnouncementId(teamMemberAnnouncement.getId())) {
            AnnouncementPosition announcementPosition =
                announcementPositionQueryAdapter.findAnnouncementPositionByTeamMemberAnnouncementId(teamMemberAnnouncement.getId());
            return teamMemberAnnouncementMapper.toAnnouncementPositionItem(announcementPosition);
        }
        return new AnnouncementPositionItem();
    }

    /**
     * 공고 스킬 정보를 조회하여 AnnouncementSkillName 리스트로 반환합니다.
     *
     * @param teamMemberAnnouncement 조회 대상 공고 엔티티.
     * @return 공고 스킬 이름 리스트, 정보가 없으면 빈 리스트 반환.
     */
    private List<AnnouncementSkillName> fetchAnnouncementSkills(final TeamMemberAnnouncement teamMemberAnnouncement) {
        if (announcementSkillQueryAdapter.existsAnnouncementSkillsByTeamMemberAnnouncementId(teamMemberAnnouncement.getId())) {
            List<AnnouncementSkill> announcementSkills =
                announcementSkillQueryAdapter.getAnnouncementSkills(teamMemberAnnouncement.getId());
            return announcementSkillMapper.toAnnouncementSkillNames(announcementSkills);
        }
        return Collections.emptyList();
    }
}
