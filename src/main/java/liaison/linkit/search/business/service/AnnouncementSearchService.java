package liaison.linkit.search.business.service;

import java.util.Collections;
import java.util.List;
import liaison.linkit.common.business.RegionMapper;
import liaison.linkit.common.implement.RegionQueryAdapter;
import liaison.linkit.common.presentation.RegionResponseDTO.RegionDetail;
import liaison.linkit.global.util.DateUtils;
import liaison.linkit.scrap.implement.announcementScrap.AnnouncementScrapQueryAdapter;
import liaison.linkit.team.business.mapper.scale.TeamScaleMapper;
import liaison.linkit.team.business.mapper.announcement.AnnouncementSkillMapper;
import liaison.linkit.team.business.mapper.announcement.TeamMemberAnnouncementMapper;
import liaison.linkit.team.domain.team.Team;
import liaison.linkit.team.domain.region.TeamRegion;
import liaison.linkit.team.domain.announcement.AnnouncementPosition;
import liaison.linkit.team.domain.announcement.AnnouncementSkill;
import liaison.linkit.team.domain.announcement.TeamMemberAnnouncement;
import liaison.linkit.team.domain.scale.TeamScale;
import liaison.linkit.team.implement.announcement.AnnouncementPositionQueryAdapter;
import liaison.linkit.team.implement.announcement.AnnouncementSkillQueryAdapter;
import liaison.linkit.team.implement.announcement.TeamMemberAnnouncementQueryAdapter;
import liaison.linkit.team.implement.scale.TeamScaleQueryAdapter;
import liaison.linkit.team.presentation.announcement.dto.TeamMemberAnnouncementResponseDTO;
import liaison.linkit.team.presentation.announcement.dto.TeamMemberAnnouncementResponseDTO.AnnouncementInformMenu;
import liaison.linkit.team.presentation.announcement.dto.TeamMemberAnnouncementResponseDTO.AnnouncementPositionItem;
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
public class AnnouncementSearchService {

    private final TeamMemberAnnouncementQueryAdapter teamMemberAnnouncementQueryAdapter;
    private final AnnouncementPositionQueryAdapter announcementPositionQueryAdapter;
    private final TeamMemberAnnouncementMapper teamMemberAnnouncementMapper;
    private final AnnouncementSkillQueryAdapter announcementSkillQueryAdapter;
    private final AnnouncementScrapQueryAdapter announcementScrapQueryAdapter;
    private final AnnouncementSkillMapper announcementSkillMapper;
    private final TeamScaleQueryAdapter teamScaleQueryAdapter;
    private final TeamScaleMapper teamScaleMapper;
    private final RegionQueryAdapter regionQueryAdapter;
    private final RegionMapper regionMapper;

    // 로그인 상태에서 조회
    public Page<AnnouncementInformMenu> searchAnnouncementsInLoginState(
            final Long memberId,
            List<String> majorPosition,
            List<String> skillName,
            List<String> cityName,
            List<String> scaleName,
            Pageable pageable
    ) {
        Page<TeamMemberAnnouncement> announcements = teamMemberAnnouncementQueryAdapter.findAll(majorPosition, skillName, cityName, scaleName, pageable);

        log.info(announcements.toString());

        return announcements.map(
                teamMemberAnnouncement -> toSearchAnnouncementInformInLoginState(memberId, teamMemberAnnouncement)
        );
    }

    // 로그아웃 상태에서 조회
    public Page<AnnouncementInformMenu> searchAnnouncementsInLogoutState(
            List<String> majorPosition,
            List<String> skillName,
            List<String> cityName,
            List<String> scaleName,
            Pageable pageable
    ) {
        Page<TeamMemberAnnouncement> announcements = teamMemberAnnouncementQueryAdapter.findAll(majorPosition, skillName, cityName, scaleName, pageable);
        return announcements.map(this::toSearchAnnouncementInformInLogoutState);
    }

    private AnnouncementInformMenu toSearchAnnouncementInformInLogoutState(
            final TeamMemberAnnouncement teamMemberAnnouncement
    ) {
        final Team team = teamMemberAnnouncement.getTeam();
        log.info("error 1");
        // 팀 규모 조회
        TeamScaleItem teamScaleItem = null;
        if (teamScaleQueryAdapter.existsTeamScaleByTeamId(team.getId())) {
            final TeamScale teamScale = teamScaleQueryAdapter.findTeamScaleByTeamId(team.getId());
            teamScaleItem = teamScaleMapper.toTeamScaleItem(teamScale);
        }
        log.info("error 2");
        // 팀 지역 조회
        RegionDetail regionDetail = new RegionDetail();
        if (regionQueryAdapter.existsTeamRegionByTeamId((team.getId()))) {
            final TeamRegion teamRegion = regionQueryAdapter.findTeamRegionByTeamId(team.getId());
            regionDetail = regionMapper.toRegionDetail(teamRegion.getRegion());
        }
        log.info("error 3");
        // 포지션 조회
        log.info("error 3.1.1.");
        AnnouncementPositionItem announcementPositionItem = new AnnouncementPositionItem();
        log.info("error 3.1.2.");
        if (announcementPositionQueryAdapter.existsAnnouncementPositionByTeamMemberAnnouncementId(teamMemberAnnouncement.getId())) {
            log.info("error 3.1");
            AnnouncementPosition announcementPosition = announcementPositionQueryAdapter.findAnnouncementPositionByTeamMemberAnnouncementId(teamMemberAnnouncement.getId());
            log.info("error 3.2");
            announcementPositionItem = teamMemberAnnouncementMapper.toAnnouncementPositionItem(announcementPosition);
        }
        log.info("error 4");
        // 스킬 조회
        List<TeamMemberAnnouncementResponseDTO.AnnouncementSkillName> announcementSkillNames = Collections.emptyList();
        if (announcementSkillQueryAdapter.existsAnnouncementSkillsByTeamMemberAnnouncementId(teamMemberAnnouncement.getId())) {
            List<AnnouncementSkill> announcementSkills = announcementSkillQueryAdapter.getAnnouncementSkills(teamMemberAnnouncement.getId());
            announcementSkillNames = announcementSkillMapper.toAnnouncementSkillNames(announcementSkills);
        }

        int announcementDDay = -1;
        if (!teamMemberAnnouncement.isPermanentRecruitment() && teamMemberAnnouncement.getAnnouncementEndDate() != null) {
            announcementDDay = DateUtils.calculateDDay(teamMemberAnnouncement.getAnnouncementEndDate());
        }

        final int announcementScrapCount = announcementScrapQueryAdapter.getTotalAnnouncementScrapCount(teamMemberAnnouncement.getId());

        log.info("error 6");
        return teamMemberAnnouncementMapper.toTeamMemberAnnouncementInform(
                team.getTeamLogoImagePath(),
                team.getTeamName(),
                team.getTeamCode(),
                teamScaleItem,
                regionDetail,
                teamMemberAnnouncement,
                announcementDDay,
                false,
                announcementScrapCount,
                announcementPositionItem,
                announcementSkillNames
        );
    }


    private AnnouncementInformMenu toSearchAnnouncementInformInLoginState(
            final Long memberId,
            final TeamMemberAnnouncement teamMemberAnnouncement
    ) {
        final Team team = teamMemberAnnouncement.getTeam();

        log.info("error 1");

        // 팀 규모 조회
        TeamScaleItem teamScaleItem = null;
        if (teamScaleQueryAdapter.existsTeamScaleByTeamId(team.getId())) {
            final TeamScale teamScale = teamScaleQueryAdapter.findTeamScaleByTeamId(team.getId());
            teamScaleItem = teamScaleMapper.toTeamScaleItem(teamScale);
        }

        log.info("error 2");

        // 팀 지역 조회
        RegionDetail regionDetail = new RegionDetail();
        if (regionQueryAdapter.existsTeamRegionByTeamId((team.getId()))) {
            final TeamRegion teamRegion = regionQueryAdapter.findTeamRegionByTeamId(team.getId());
            regionDetail = regionMapper.toRegionDetail(teamRegion.getRegion());
        }

        log.info("error 3");

        // 포지션 조회
        AnnouncementPositionItem announcementPositionItem = new AnnouncementPositionItem();
        if (announcementPositionQueryAdapter.existsAnnouncementPositionByTeamMemberAnnouncementId(teamMemberAnnouncement.getId())) {
            AnnouncementPosition announcementPosition = announcementPositionQueryAdapter.findAnnouncementPositionByTeamMemberAnnouncementId(teamMemberAnnouncement.getId());
            announcementPositionItem = teamMemberAnnouncementMapper.toAnnouncementPositionItem(announcementPosition);
        }

        // 스킬 조회
        List<TeamMemberAnnouncementResponseDTO.AnnouncementSkillName> announcementSkillNames = Collections.emptyList();
        if (announcementSkillQueryAdapter.existsAnnouncementSkillsByTeamMemberAnnouncementId(teamMemberAnnouncement.getId())) {
            List<AnnouncementSkill> announcementSkills = announcementSkillQueryAdapter.getAnnouncementSkills(teamMemberAnnouncement.getId());
            announcementSkillNames = announcementSkillMapper.toAnnouncementSkillNames(announcementSkills);
        }

        int announcementDDay = -1;
        if (!teamMemberAnnouncement.isPermanentRecruitment() && teamMemberAnnouncement.getAnnouncementEndDate() != null) {
            announcementDDay = DateUtils.calculateDDay(teamMemberAnnouncement.getAnnouncementEndDate());
        }

        final boolean isAnnouncementScrap = announcementScrapQueryAdapter.existsByMemberIdAndTeamMemberAnnouncementId(memberId, teamMemberAnnouncement.getId());
        final int announcementScrapCount = announcementScrapQueryAdapter.getTotalAnnouncementScrapCount(teamMemberAnnouncement.getId());

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

}
