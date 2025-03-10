package liaison.linkit.team.business.assembler.common;

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
import liaison.linkit.team.implement.scale.TeamScaleQueryAdapter;
import liaison.linkit.team.presentation.announcement.dto.TeamMemberAnnouncementResponseDTO.AnnouncementPositionItem;
import liaison.linkit.team.presentation.announcement.dto.TeamMemberAnnouncementResponseDTO.AnnouncementSkillName;
import liaison.linkit.team.presentation.team.dto.TeamResponseDTO.TeamScaleItem;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class AnnouncementCommonAssembler {

    // Adapters
    private final RegionQueryAdapter regionQueryAdapter;
    private final TeamScaleQueryAdapter teamScaleQueryAdapter;
    private final AnnouncementSkillQueryAdapter announcementSkillQueryAdapter;
    private final AnnouncementPositionQueryAdapter announcementPositionQueryAdapter;
    private final AnnouncementScrapQueryAdapter announcementScrapQueryAdapter;

    // Mappers
    private final RegionMapper regionMapper;
    private final TeamScaleMapper teamScaleMapper;
    private final TeamMemberAnnouncementMapper teamMemberAnnouncementMapper;
    private final AnnouncementSkillMapper announcementSkillMapper;

    /**
     * 팀 규모 정보를 조회하여 TeamScaleItem으로 반환합니다. 실제 구현에서는 teamScaleQueryAdapter와 teamScaleMapper를 사용합니다.
     *
     * @param team 조회 대상 팀.
     * @return 팀 규모 정보 DTO, 정보가 없으면 null 반환.
     */
    public TeamScaleItem fetchTeamScaleItem(final Team team) {
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
    public RegionDetail fetchRegionDetail(final Team team) {
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
    public AnnouncementPositionItem fetchAnnouncementPositionItem(
            final TeamMemberAnnouncement teamMemberAnnouncement) {
        if (announcementPositionQueryAdapter.existsAnnouncementPositionByTeamMemberAnnouncementId(
                teamMemberAnnouncement.getId())) {
            AnnouncementPosition announcementPosition =
                    announcementPositionQueryAdapter
                            .findAnnouncementPositionByTeamMemberAnnouncementId(
                                    teamMemberAnnouncement.getId());
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
    public List<AnnouncementSkillName> fetchAnnouncementSkills(
            final TeamMemberAnnouncement teamMemberAnnouncement) {
        if (announcementSkillQueryAdapter.existsAnnouncementSkillsByTeamMemberAnnouncementId(
                teamMemberAnnouncement.getId())) {
            List<AnnouncementSkill> announcementSkills =
                    announcementSkillQueryAdapter.getAnnouncementSkills(
                            teamMemberAnnouncement.getId());
            return announcementSkillMapper.toAnnouncementSkillNames(announcementSkills);
        }
        return Collections.emptyList();
    }

    /**
     * 로그인 상태인 경우, 공고 스크랩 여부를 조회합니다. 로그아웃 상태이면 false를 반환합니다.
     *
     * @param teamMemberAnnouncement 조회 대상 공고 엔티티.
     * @param optionalMemberId 로그인한 회원의 ID(Optional).
     * @return 공고가 스크랩된 상태이면 true, 아니면 false.
     */
    public boolean checkAnnouncementScrap(
            final TeamMemberAnnouncement teamMemberAnnouncement,
            final Optional<Long> optionalMemberId) {
        return optionalMemberId
                .map(
                        memberId ->
                                announcementScrapQueryAdapter
                                        .existsByMemberIdAndTeamMemberAnnouncementId(
                                                memberId, teamMemberAnnouncement.getId()))
                .orElse(false);
    }

    /**
     * 공고의 스크랩 수를 조회합니다.
     *
     * @param teamMemberAnnouncement 조회 대상 공고 엔티티.
     * @return 공고의 총 스크랩 수.
     */
    public int getAnnouncementScrapCount(final TeamMemberAnnouncement teamMemberAnnouncement) {
        return announcementScrapQueryAdapter.getTotalAnnouncementScrapCount(
                teamMemberAnnouncement.getId());
    }

    /**
     * 포지션 정보를 바탕으로 대분류를 추출합니다.
     *
     * @param announcementPosition 조회된 공고 포지션.
     * @return 대분류 문자열; 정보가 없으면 "N/A" 반환.
     */
    public String extractMajorPosition(final AnnouncementPosition announcementPosition) {
        if (announcementPosition != null && announcementPosition.getPosition() != null) {
            return announcementPosition.getPosition().getMajorPosition();
        }
        return "N/A";
    }

    /**
     * 공고의 D-Day를 계산합니다. 공고가 상시 모집이 아니고 종료 날짜가 존재하면 D-Day를 계산하고, 그렇지 않으면 -1을 반환합니다.
     *
     * @param teamMemberAnnouncement 조회 대상 공고 엔티티.
     * @return 계산된 D-Day 값.
     */
    public int calculateAnnouncementDDay(final TeamMemberAnnouncement teamMemberAnnouncement) {
        if (!teamMemberAnnouncement.isPermanentRecruitment()
                && teamMemberAnnouncement.getAnnouncementEndDate() != null) {
            return DateUtils.calculateDDay(teamMemberAnnouncement.getAnnouncementEndDate());
        }
        return -1;
    }

    // 공고의 마감 여부를 계산한다.
    public boolean calculateAnnouncementIsClosed(
            final TeamMemberAnnouncement teamMemberAnnouncement) {

        // (제일 강력한 변수 값) isAnnouncementInProgress
        if (!teamMemberAnnouncement.isAnnouncementInProgress()) {
            // 공고가 마감된 상태
            return true;
        }

        // 상시 모집이 아니면서 공고 종료일이 존재하는 경우
        if (!teamMemberAnnouncement.isPermanentRecruitment()
                && teamMemberAnnouncement.getAnnouncementEndDate() != null) {
            return DateUtils.calculateAnnouncementClosed(
                    teamMemberAnnouncement.getAnnouncementEndDate());
        }

        return false;
    }
}
