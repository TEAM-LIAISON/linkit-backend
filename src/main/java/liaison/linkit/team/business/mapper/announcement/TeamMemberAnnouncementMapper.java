package liaison.linkit.team.business.mapper.announcement;

import java.util.ArrayList;
import java.util.List;

import liaison.linkit.common.annotation.Mapper;
import liaison.linkit.common.presentation.RegionResponseDTO.RegionDetail;
import liaison.linkit.global.util.DateUtils;
import liaison.linkit.team.domain.announcement.AnnouncementPosition;
import liaison.linkit.team.domain.announcement.TeamMemberAnnouncement;
import liaison.linkit.team.domain.team.Team;
import liaison.linkit.team.presentation.announcement.dto.TeamMemberAnnouncementRequestDTO.AddTeamMemberAnnouncementRequest;
import liaison.linkit.team.presentation.announcement.dto.TeamMemberAnnouncementResponseDTO;
import liaison.linkit.team.presentation.announcement.dto.TeamMemberAnnouncementResponseDTO.AddTeamMemberAnnouncementResponse;
import liaison.linkit.team.presentation.announcement.dto.TeamMemberAnnouncementResponseDTO.AnnouncementInformMenu;
import liaison.linkit.team.presentation.announcement.dto.TeamMemberAnnouncementResponseDTO.AnnouncementInformMenus;
import liaison.linkit.team.presentation.announcement.dto.TeamMemberAnnouncementResponseDTO.AnnouncementPositionItem;
import liaison.linkit.team.presentation.announcement.dto.TeamMemberAnnouncementResponseDTO.AnnouncementSkillName;
import liaison.linkit.team.presentation.announcement.dto.TeamMemberAnnouncementResponseDTO.TeamMemberAnnouncementDetail;
import liaison.linkit.team.presentation.announcement.dto.TeamMemberAnnouncementResponseDTO.TeamMemberAnnouncementItem;
import liaison.linkit.team.presentation.announcement.dto.TeamMemberAnnouncementResponseDTO.UpdateTeamMemberAnnouncementResponse;
import liaison.linkit.team.presentation.team.dto.TeamResponseDTO.TeamScaleItem;

@Mapper
public class TeamMemberAnnouncementMapper {

    public AnnouncementInformMenu toTeamMemberAnnouncementInform(
            final String teamLogoImagePath,
            final String teamName,
            final String teamCode,
            final TeamScaleItem teamScaleItem,
            final RegionDetail regionDetail,
            final TeamMemberAnnouncement teamMemberAnnouncement,
            final int announcementDDay,
            final boolean isClosed,
            final boolean isAnnouncementScrap,
            final int announcementScrapCount,
            final AnnouncementPositionItem announcementPositionItem,
            final List<AnnouncementSkillName> announcementSkillNames) {
        return AnnouncementInformMenu.builder()
                .teamMemberAnnouncementId(
                        teamMemberAnnouncement != null ? teamMemberAnnouncement.getId() : 0L)
                .teamLogoImagePath(teamLogoImagePath != null ? teamLogoImagePath : "")
                .teamName(teamName != null ? teamName : "")
                .teamCode(teamCode != null ? teamCode : "")
                .teamScaleItem(teamScaleItem != null ? teamScaleItem : new TeamScaleItem())
                .regionDetail(regionDetail != null ? regionDetail : new RegionDetail())
                .announcementDDay(announcementDDay)
                .isClosed(isClosed)
                .isPermanentRecruitment(
                        teamMemberAnnouncement != null
                                && teamMemberAnnouncement.isPermanentRecruitment())
                .announcementTitle(
                        teamMemberAnnouncement != null
                                ? teamMemberAnnouncement.getAnnouncementTitle()
                                : "")
                .isAnnouncementScrap(isAnnouncementScrap)
                .announcementScrapCount(announcementScrapCount)
                .viewCount(
                        teamMemberAnnouncement != null ? teamMemberAnnouncement.getViewCount() : 0L)
                .createdAt(
                        teamMemberAnnouncement != null
                                ? DateUtils.formatRelativeTime(
                                        teamMemberAnnouncement.getCreatedAt())
                                : null)
                .announcementPositionItem(
                        announcementPositionItem != null
                                ? announcementPositionItem
                                : new AnnouncementPositionItem())
                .announcementSkillNames(
                        announcementSkillNames != null ? announcementSkillNames : new ArrayList<>())
                .build();
    }

    public AnnouncementInformMenus toAnnouncementInformMenus(
            final List<AnnouncementInformMenu> announcementInformMenus) {
        return AnnouncementInformMenus.builder()
                .announcementInformMenus(announcementInformMenus)
                .build();
    }

    public TeamMemberAnnouncementResponseDTO.TeamMemberAnnouncementDetail
            toTeamMemberAnnouncementDetail(
                    final TeamMemberAnnouncement teamMemberAnnouncement,
                    final boolean isMyTeamAnnouncement,
                    final boolean isAnnouncementScrap,
                    final int announcementScrapCount,
                    final AnnouncementPositionItem announcementPositionItem,
                    final List<AnnouncementSkillName> announcementSkillNames) {
        // D-Day 계산 (isPermanentRecruitment가 true이면 -1, 아니면 정상 계산)
        int announcementDDay = -1;
        boolean isClosed = false;
        if (!teamMemberAnnouncement.isPermanentRecruitment()
                && teamMemberAnnouncement.getAnnouncementEndDate() != null) {
            announcementDDay =
                    DateUtils.calculateDDay(teamMemberAnnouncement.getAnnouncementEndDate());

            isClosed =
                    DateUtils.calculateAnnouncementClosed(
                            teamMemberAnnouncement.getAnnouncementEndDate());
        }

        return TeamMemberAnnouncementDetail.builder()
                .teamMemberAnnouncementId(teamMemberAnnouncement.getId())
                .isMyTeamAnnouncement(isMyTeamAnnouncement)
                .isAnnouncementScrap(isAnnouncementScrap)
                .announcementScrapCount(announcementScrapCount)
                .announcementDDay(announcementDDay) // 수정된 부분
                .isClosed(isClosed)
                .announcementEndDate(teamMemberAnnouncement.getAnnouncementEndDate())
                .announcementTitle(teamMemberAnnouncement.getAnnouncementTitle())
                .viewCount(teamMemberAnnouncement.getViewCount())
                .createdAt(teamMemberAnnouncement.getCreatedAt())
                .announcementPositionItem(announcementPositionItem)
                .announcementSkillNames(announcementSkillNames)
                .isPermanentRecruitment(teamMemberAnnouncement.isPermanentRecruitment())
                .isRegionFlexible(teamMemberAnnouncement.isRegionFlexible())
                .mainTasks(teamMemberAnnouncement.getMainTasks())
                .workMethod(teamMemberAnnouncement.getWorkMethod())
                .idealCandidate(teamMemberAnnouncement.getIdealCandidate())
                .preferredQualifications(teamMemberAnnouncement.getPreferredQualifications())
                .joiningProcess(teamMemberAnnouncement.getJoiningProcess())
                .benefits(teamMemberAnnouncement.getBenefits())
                .isLegacyAnnouncement(teamMemberAnnouncement.isLegacyAnnouncement())
                .build();
    }

    public TeamMemberAnnouncement toAddTeamMemberAnnouncement(
            final Team team, final AddTeamMemberAnnouncementRequest request) {
        return TeamMemberAnnouncement.builder()
                .id(null)
                .team(team)
                .announcementTitle(request.getAnnouncementTitle())
                .announcementEndDate(request.getAnnouncementEndDate())
                .isPermanentRecruitment(request.getIsPermanentRecruitment())
                .isRegionFlexible(request.getIsRegionFlexible())
                .mainTasks(request.getMainTasks())
                .workMethod(request.getWorkMethod())
                .idealCandidate(request.getIdealCandidate())
                .preferredQualifications(request.getPreferredQualifications())
                .joiningProcess(request.getJoiningProcess())
                .benefits(request.getBenefits())
                .isAnnouncementPublic(true)
                .isAnnouncementInProgress(true)
                .isLegacyAnnouncement(false)
                .viewCount(0L)
                .build();
    }

    public TeamMemberAnnouncementItem toTeamMemberAnnouncementItem(
            final TeamMemberAnnouncement teamMemberAnnouncement,
            final int announcementDDay,
            final boolean isClosed,
            final String majorPosition,
            final List<AnnouncementSkillName> announcementSkillNames,
            final boolean isAnnouncementScrap,
            final int announcementScrapCount) {
        return TeamMemberAnnouncementItem.builder()
                .teamMemberAnnouncementId(teamMemberAnnouncement.getId())
                .announcementDDay(announcementDDay)
                .isClosed(isClosed)
                .isPermanentRecruitment(teamMemberAnnouncement.isPermanentRecruitment())
                .announcementTitle(teamMemberAnnouncement.getAnnouncementTitle())
                .majorPosition(majorPosition)
                .announcementSkillNames(announcementSkillNames)
                .isAnnouncementPublic(teamMemberAnnouncement.isAnnouncementPublic())
                .isAnnouncementInProgress(teamMemberAnnouncement.isAnnouncementInProgress())
                .isAnnouncementScrap(isAnnouncementScrap)
                .announcementScrapCount(announcementScrapCount)
                .build();
    }

    public TeamMemberAnnouncementResponseDTO.AnnouncementPositionItem toAnnouncementPositionItem(
            final AnnouncementPosition announcementPosition) {
        return AnnouncementPositionItem.builder()
                .majorPosition(announcementPosition.getPosition().getMajorPosition())
                .subPosition(announcementPosition.getPosition().getSubPosition())
                .build();
    }

    public TeamMemberAnnouncementResponseDTO.AddTeamMemberAnnouncementResponse
            toAddTeamMemberAnnouncementResponse(
                    final TeamMemberAnnouncement teamMemberAnnouncement,
                    final AnnouncementPositionItem announcementPositionItem,
                    final List<AnnouncementSkillName> announcementSkillNames) {
        return AddTeamMemberAnnouncementResponse.builder()
                .teamMemberAnnouncementId(teamMemberAnnouncement.getId())
                .announcementTitle(teamMemberAnnouncement.getAnnouncementTitle())
                .announcementPositionItem(announcementPositionItem)
                .announcementSkillNames(announcementSkillNames)
                .announcementEndDate(teamMemberAnnouncement.getAnnouncementEndDate())
                .isPermanentRecruitment(teamMemberAnnouncement.isPermanentRecruitment())
                .isRegionFlexible(teamMemberAnnouncement.isRegionFlexible())
                .mainTasks(teamMemberAnnouncement.getMainTasks())
                .workMethod(teamMemberAnnouncement.getWorkMethod())
                .idealCandidate(teamMemberAnnouncement.getIdealCandidate())
                .preferredQualifications(teamMemberAnnouncement.getPreferredQualifications())
                .joiningProcess(teamMemberAnnouncement.getJoiningProcess())
                .benefits(teamMemberAnnouncement.getBenefits())
                .isLegacyAnnouncement(teamMemberAnnouncement.isLegacyAnnouncement())
                .build();
    }

    public TeamMemberAnnouncementResponseDTO.UpdateTeamMemberAnnouncementResponse
            toUpdateTeamMemberAnnouncementResponse(
                    final TeamMemberAnnouncement teamMemberAnnouncement,
                    final AnnouncementPositionItem announcementPositionItem,
                    final List<AnnouncementSkillName> announcementSkillNames) {
        return UpdateTeamMemberAnnouncementResponse.builder()
                .teamMemberAnnouncementId(teamMemberAnnouncement.getId())
                .announcementTitle(teamMemberAnnouncement.getAnnouncementTitle())
                .announcementPositionItem(announcementPositionItem)
                .announcementSkillNames(announcementSkillNames)
                .announcementEndDate(teamMemberAnnouncement.getAnnouncementEndDate())
                .isPermanentRecruitment(teamMemberAnnouncement.isPermanentRecruitment())
                .isRegionFlexible(teamMemberAnnouncement.isRegionFlexible())
                .mainTasks(teamMemberAnnouncement.getMainTasks())
                .workMethod(teamMemberAnnouncement.getWorkMethod())
                .idealCandidate(teamMemberAnnouncement.getIdealCandidate())
                .preferredQualifications(teamMemberAnnouncement.getPreferredQualifications())
                .joiningProcess(teamMemberAnnouncement.getJoiningProcess())
                .benefits(teamMemberAnnouncement.getBenefits())
                .isLegacyAnnouncement(teamMemberAnnouncement.isLegacyAnnouncement())
                .build();
    }

    public TeamMemberAnnouncementResponseDTO.RemoveTeamMemberAnnouncementResponse
            toRemoveTeamMemberAnnouncementId(final Long teamMemberAnnouncementId) {
        return TeamMemberAnnouncementResponseDTO.RemoveTeamMemberAnnouncementResponse.builder()
                .teamMemberAnnouncementId(teamMemberAnnouncementId)
                .build();
    }

    public TeamMemberAnnouncementResponseDTO.UpdateTeamMemberAnnouncementPublicStateResponse
            toUpdateTeamMemberAnnouncementPublicState(
                    final TeamMemberAnnouncement teamMemberAnnouncement) {
        return TeamMemberAnnouncementResponseDTO.UpdateTeamMemberAnnouncementPublicStateResponse
                .builder()
                .teamMemberAnnouncementId(teamMemberAnnouncement.getId())
                .isAnnouncementPublic(teamMemberAnnouncement.isAnnouncementPublic())
                .build();
    }

    public TeamMemberAnnouncementResponseDTO.CloseTeamMemberAnnouncementResponse
            toCloseTeamMemberAnnouncement(final TeamMemberAnnouncement teamMemberAnnouncement) {
        return TeamMemberAnnouncementResponseDTO.CloseTeamMemberAnnouncementResponse.builder()
                .teamMemberAnnouncementId(teamMemberAnnouncement.getId())
                .isAnnouncementInProgress(teamMemberAnnouncement.isAnnouncementInProgress())
                .build();
    }
}
