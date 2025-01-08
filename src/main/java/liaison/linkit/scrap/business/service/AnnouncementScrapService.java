package liaison.linkit.scrap.business.service;

import java.util.ArrayList;
import java.util.List;
import liaison.linkit.common.business.RegionMapper;
import liaison.linkit.common.implement.RegionQueryAdapter;
import liaison.linkit.common.presentation.RegionResponseDTO.RegionDetail;
import liaison.linkit.global.util.DateUtils;
import liaison.linkit.member.domain.Member;
import liaison.linkit.member.implement.MemberQueryAdapter;
import liaison.linkit.scrap.business.mapper.AnnouncementScrapMapper;
import liaison.linkit.scrap.domain.AnnouncementScrap;
import liaison.linkit.scrap.exception.announcementScrap.AnnouncementScrapBadRequestException;
import liaison.linkit.scrap.exception.profileScrap.ProfileScrapBadRequestException;
import liaison.linkit.scrap.implement.announcementScrap.AnnouncementScrapCommandAdapter;
import liaison.linkit.scrap.implement.announcementScrap.AnnouncementScrapQueryAdapter;
import liaison.linkit.scrap.presentation.dto.announcementScrap.AnnouncementScrapRequestDTO.UpdateAnnouncementScrapRequest;
import liaison.linkit.scrap.presentation.dto.announcementScrap.AnnouncementScrapResponseDTO;
import liaison.linkit.scrap.validation.ScrapValidator;
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
import liaison.linkit.team.presentation.announcement.dto.TeamMemberAnnouncementResponseDTO;
import liaison.linkit.team.presentation.announcement.dto.TeamMemberAnnouncementResponseDTO.AnnouncementInformMenu;
import liaison.linkit.team.presentation.announcement.dto.TeamMemberAnnouncementResponseDTO.AnnouncementInformMenus;
import liaison.linkit.team.presentation.announcement.dto.TeamMemberAnnouncementResponseDTO.AnnouncementPositionItem;
import liaison.linkit.team.presentation.team.dto.TeamResponseDTO.TeamScaleItem;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class AnnouncementScrapService {

    private final MemberQueryAdapter memberQueryAdapter;
    private final TeamMemberAnnouncementQueryAdapter teamMemberAnnouncementQueryAdapter;
    private final AnnouncementScrapQueryAdapter announcementScrapQueryAdapter;
    private final AnnouncementScrapCommandAdapter announcementScrapCommandAdapter;

    private final AnnouncementScrapMapper announcementScrapMapper;

    private final ScrapValidator scrapValidator;
    private final TeamScaleQueryAdapter teamScaleQueryAdapter;
    private final TeamScaleMapper teamScaleMapper;
    private final RegionQueryAdapter regionQueryAdapter;
    private final RegionMapper regionMapper;
    private final AnnouncementPositionQueryAdapter announcementPositionQueryAdapter;
    private final TeamMemberAnnouncementMapper teamMemberAnnouncementMapper;
    private final AnnouncementSkillQueryAdapter announcementSkillQueryAdapter;
    private final AnnouncementSkillMapper announcementSkillMapper;

    public AnnouncementScrapResponseDTO.UpdateAnnouncementScrap updateAnnouncementScrap(
            final Long memberId,
            final Long teamMemberAnnouncementId,
            final UpdateAnnouncementScrapRequest updateAnnouncementScrapRequest
    ) {

        boolean shouldAddScrap = updateAnnouncementScrapRequest.isChangeScrapValue();

        scrapValidator.validateSelfTeamMemberAnnouncementScrap(memberId, teamMemberAnnouncementId);     // 자기 자신의 프로필 선택에 대한 예외 처리
        scrapValidator.validateMemberMaxTeamMemberAnnouncementScrap(memberId);         // 최대 프로필 스크랩 개수에 대한 예외 처리

        boolean scrapExists = announcementScrapQueryAdapter.existsByMemberIdAndTeamMemberAnnouncementId(memberId, teamMemberAnnouncementId);

        if (scrapExists) {
            handleExistingScrap(memberId, teamMemberAnnouncementId, shouldAddScrap);
        } else {
            handleNonExistingScrap(memberId, teamMemberAnnouncementId, shouldAddScrap);
        }

        return announcementScrapMapper.toUpdateAnnouncementScrap(teamMemberAnnouncementId, shouldAddScrap);
    }

    public AnnouncementInformMenus getAnnouncementScraps(
            final Long memberId
    ) {
        // 1) memberId로 Announcement 목록 조회
        final List<AnnouncementScrap> announcementScraps = announcementScrapQueryAdapter.findAllByMemberId(memberId);

        // 2) TeamScrap -> Team 리스트 추출
        final List<TeamMemberAnnouncement> teamMemberAnnouncements = announcementScraps.stream()
                .map(AnnouncementScrap::getTeamMemberAnnouncement)
                .toList();

        final List<AnnouncementInformMenu> announcementInformMenus = new ArrayList<>();

        for (TeamMemberAnnouncement teamMemberAnnouncement : teamMemberAnnouncements) {
            final Team team = teamMemberAnnouncement.getTeam();

            // 팀 규모 조회
            TeamScaleItem teamScaleItem = null;
            if (teamScaleQueryAdapter.existsTeamScaleByTeamId(team.getId())) {
                final TeamScale teamScale = teamScaleQueryAdapter.findTeamScaleByTeamId(team.getId());
                teamScaleItem = teamScaleMapper.toTeamScaleItem(teamScale);
            }

            // 팀 지역 조회
            RegionDetail regionDetail = new RegionDetail();
            if (regionQueryAdapter.existsTeamRegionByTeamId((team.getId()))) {
                final TeamRegion teamRegion = regionQueryAdapter.findTeamRegionByTeamId(team.getId());
                regionDetail = regionMapper.toRegionDetail(teamRegion.getRegion());
            }

            // 포지션 조회
            AnnouncementPositionItem announcementPositionItem = new AnnouncementPositionItem();
            if (announcementPositionQueryAdapter.existsAnnouncementPositionByTeamMemberAnnouncementId(teamMemberAnnouncement.getId())) {
                AnnouncementPosition announcementPosition = announcementPositionQueryAdapter.findAnnouncementPositionByTeamMemberAnnouncementId(teamMemberAnnouncement.getId());
                announcementPositionItem = teamMemberAnnouncementMapper.toAnnouncementPositionItem(announcementPosition);
            }

            // 스킬 조회
            List<AnnouncementSkill> announcementSkills = announcementSkillQueryAdapter.getAnnouncementSkills(teamMemberAnnouncement.getId());
            List<TeamMemberAnnouncementResponseDTO.AnnouncementSkillName> announcementSkillNames = announcementSkillMapper.toAnnouncementSkillNames(announcementSkills);

            final int announcementDDay = DateUtils.calculateDDay(teamMemberAnnouncement.getAnnouncementEndDate());
            final boolean isAnnouncementScrap = announcementScrapQueryAdapter.existsByMemberIdAndTeamMemberAnnouncementId(memberId, teamMemberAnnouncement.getId());
            final int announcementScrapCount = announcementScrapQueryAdapter.getTotalAnnouncementScrapCount(teamMemberAnnouncement.getId());

            final AnnouncementInformMenu announcementInformMenu = teamMemberAnnouncementMapper.toTeamMemberAnnouncementInform(
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
            announcementInformMenus.add(announcementInformMenu);
        }

        return announcementScrapMapper.toAnnouncementInformMenus(announcementInformMenus);
    }

    // 스크랩이 존재하는 경우 처리 메서드
    private void handleExistingScrap(Long memberId, Long teamMemberAnnouncementId, boolean shouldAddScrap) {
        if (!shouldAddScrap) {
            announcementScrapCommandAdapter.deleteByMemberIdAndTeamMemberAnnouncementId(memberId, teamMemberAnnouncementId);
        } else {
            throw AnnouncementScrapBadRequestException.EXCEPTION;
        }
    }

    // 스크랩이 존재하지 않는 경우 처리 메서드
    private void handleNonExistingScrap(Long memberId, Long teamMemberAnnouncementId, boolean shouldAddScrap) {
        if (shouldAddScrap) {
            Member member = memberQueryAdapter.findById(memberId);
            TeamMemberAnnouncement teamMemberAnnouncement = teamMemberAnnouncementQueryAdapter.findById(teamMemberAnnouncementId);
            AnnouncementScrap announcementScrap = new AnnouncementScrap(null, member, teamMemberAnnouncement);
            announcementScrapCommandAdapter.addAnnouncementScrap(announcementScrap);
        } else {
            throw ProfileScrapBadRequestException.EXCEPTION;
        }
    }
}
