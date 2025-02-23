package liaison.linkit.team.business.mapper.announcement;

import java.util.List;

import liaison.linkit.common.annotation.Mapper;
import liaison.linkit.common.presentation.RegionResponseDTO.RegionDetail;
import liaison.linkit.global.util.DateUtils;
import liaison.linkit.scrap.implement.announcementScrap.AnnouncementScrapQueryAdapter;
import liaison.linkit.team.domain.announcement.AnnouncementPosition;
import liaison.linkit.team.domain.announcement.AnnouncementSkill;
import liaison.linkit.team.domain.announcement.TeamMemberAnnouncement;
import liaison.linkit.team.domain.team.Team;
import liaison.linkit.team.implement.announcement.AnnouncementPositionQueryAdapter;
import liaison.linkit.team.implement.announcement.AnnouncementSkillQueryAdapter;
import liaison.linkit.team.presentation.announcement.dto.TeamMemberAnnouncementRequestDTO.AddTeamMemberAnnouncementRequest;
import liaison.linkit.team.presentation.announcement.dto.TeamMemberAnnouncementRequestDTO.UpdateTeamMemberAnnouncementRequest;
import liaison.linkit.team.presentation.announcement.dto.TeamMemberAnnouncementResponseDTO;
import liaison.linkit.team.presentation.announcement.dto.TeamMemberAnnouncementResponseDTO.AddTeamMemberAnnouncementResponse;
import liaison.linkit.team.presentation.announcement.dto.TeamMemberAnnouncementResponseDTO.AnnouncementInformMenu;
import liaison.linkit.team.presentation.announcement.dto.TeamMemberAnnouncementResponseDTO.AnnouncementInformMenus;
import liaison.linkit.team.presentation.announcement.dto.TeamMemberAnnouncementResponseDTO.AnnouncementPositionItem;
import liaison.linkit.team.presentation.announcement.dto.TeamMemberAnnouncementResponseDTO.AnnouncementSkillName;
import liaison.linkit.team.presentation.announcement.dto.TeamMemberAnnouncementResponseDTO.TeamMemberAnnouncemenItems;
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
            final boolean isAnnouncementScrap,
            final int announcementScrapCount,
            final AnnouncementPositionItem announcementPositionItem,
            final List<AnnouncementSkillName> announcementSkillNames) {

        return AnnouncementInformMenu.builder()
                .teamMemberAnnouncementId(teamMemberAnnouncement.getId())
                .teamLogoImagePath(teamLogoImagePath)
                .teamName(teamName)
                .teamCode(teamCode)
                .teamScaleItem(teamScaleItem)
                .regionDetail(regionDetail)
                .announcementDDay(announcementDDay)
                .isPermanentRecruitment(teamMemberAnnouncement.isPermanentRecruitment())
                .announcementTitle(teamMemberAnnouncement.getAnnouncementTitle())
                .isAnnouncementScrap(isAnnouncementScrap)
                .announcementScrapCount(announcementScrapCount)
                .announcementPositionItem(announcementPositionItem)
                .announcementSkillNames(announcementSkillNames)
                .build();
    }

    public TeamMemberAnnouncemenItems toLoggedOutTeamMemberAnnouncementViewItems(
            final List<TeamMemberAnnouncement> teamMemberAnnouncements,
            final AnnouncementPositionQueryAdapter announcementPositionQueryAdapter,
            final AnnouncementSkillQueryAdapter announcementSkillQueryAdapter,
            final AnnouncementSkillMapper announcementSkillMapper,
            final AnnouncementScrapQueryAdapter announcementScrapQueryAdapter) {
        List<TeamMemberAnnouncementItem> items =
                teamMemberAnnouncements.stream()
                        .map(
                                teamMemberAnnouncement -> {
                                    final AnnouncementPosition announcementPosition =
                                            announcementPositionQueryAdapter
                                                    .findAnnouncementPositionByTeamMemberAnnouncementId(
                                                            teamMemberAnnouncement.getId());

                                    final List<AnnouncementSkill> announcementSkills =
                                            announcementSkillQueryAdapter.getAnnouncementSkills(
                                                    teamMemberAnnouncement.getId());
                                    List<AnnouncementSkillName> announcementSkillNames =
                                            announcementSkillMapper.toAnnouncementSkillNames(
                                                    announcementSkills);

                                    final int announcementScrapCount =
                                            announcementScrapQueryAdapter
                                                    .getTotalAnnouncementScrapCount(
                                                            teamMemberAnnouncement.getId());

                                    // D-Day 계산 (공고 마감일이 있을 때만)
                                    int announcementDDay = -1; // 기본값 설정 (예: 마감일이 없는 경우 -1)
                                    if (teamMemberAnnouncement.getAnnouncementEndDate() != null) {
                                        announcementDDay =
                                                DateUtils.calculateDDay(
                                                        teamMemberAnnouncement
                                                                .getAnnouncementEndDate());
                                    }

                                    return toTeamMemberAnnouncementItem(
                                            teamMemberAnnouncement,
                                            announcementDDay,
                                            announcementPosition.getPosition().getMajorPosition(),
                                            announcementSkillNames,
                                            false,
                                            announcementScrapCount);
                                })
                        .toList();

        return TeamMemberAnnouncemenItems.builder().teamMemberAnnouncementItems(items).build();
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
            final boolean isAnnouncementScrap,
            final int announcementScrapCount,
            final AnnouncementPositionItem announcementPositionItem,
            final List<AnnouncementSkillName> announcementSkillNames) {
        // D-Day 계산 (isPermanentRecruitment가 true이면 -1, 아니면 정상 계산)
        int announcementDDay = -1;
        if (!teamMemberAnnouncement.isPermanentRecruitment()
                && teamMemberAnnouncement.getAnnouncementEndDate() != null) {
            announcementDDay =
                    DateUtils.calculateDDay(teamMemberAnnouncement.getAnnouncementEndDate());
        }

        return TeamMemberAnnouncementDetail.builder()
                .teamMemberAnnouncementId(teamMemberAnnouncement.getId())
                .isAnnouncementScrap(isAnnouncementScrap)
                .announcementScrapCount(announcementScrapCount)
                .announcementDDay(announcementDDay) // 수정된 부분
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
                .build();
    }

    public TeamMemberAnnouncement toUpdateTeamMemberAnnouncement(
            final Team team, final UpdateTeamMemberAnnouncementRequest request) {
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
                .build();
    }

    public TeamMemberAnnouncementItem toTeamMemberAnnouncementItem(
            final TeamMemberAnnouncement teamMemberAnnouncement,
            final int announcementDDay,
            final String majorPosition,
            final List<AnnouncementSkillName> announcementSkillNames,
            final boolean isAnnouncementScrap,
            final int announcementScrapCount) {
        return TeamMemberAnnouncementItem.builder()
                .teamMemberAnnouncementId(teamMemberAnnouncement.getId())
                .announcementDDay(announcementDDay)
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
}
