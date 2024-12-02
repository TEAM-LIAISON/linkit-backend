package liaison.linkit.team.business.announcement;

import java.util.List;
import java.util.stream.Collectors;
import liaison.linkit.common.annotation.Mapper;
import liaison.linkit.common.presentation.RegionResponseDTO.RegionDetail;
import liaison.linkit.team.domain.Team;
import liaison.linkit.team.domain.announcement.AnnouncementPosition;
import liaison.linkit.team.domain.announcement.TeamMemberAnnouncement;
import liaison.linkit.team.presentation.announcement.dto.TeamMemberAnnouncementRequestDTO.AddTeamMemberAnnouncementRequest;
import liaison.linkit.team.presentation.announcement.dto.TeamMemberAnnouncementResponseDTO;
import liaison.linkit.team.presentation.announcement.dto.TeamMemberAnnouncementResponseDTO.AddTeamMemberAnnouncementResponse;
import liaison.linkit.team.presentation.announcement.dto.TeamMemberAnnouncementResponseDTO.AnnouncementPositionItem;
import liaison.linkit.team.presentation.announcement.dto.TeamMemberAnnouncementResponseDTO.TeamMemberAnnouncementDetail;
import liaison.linkit.team.presentation.announcement.dto.TeamMemberAnnouncementResponseDTO.TeamMemberAnnouncementItem;
import liaison.linkit.team.presentation.announcement.dto.TeamMemberAnnouncementResponseDTO.UpdateTeamMemberAnnouncementResponse;

@Mapper
public class TeamMemberAnnouncementMapper {

    public TeamMemberAnnouncementResponseDTO.TeamMemberAnnouncementDetail toTeamMemberAnnouncementDetail(
            final TeamMemberAnnouncement teamMemberAnnouncement,
            final AnnouncementPositionItem announcementPositionItem,
            final List<TeamMemberAnnouncementResponseDTO.AnnouncementSkillName> announcementSkillNames,
            final RegionDetail regionDetail
    ) {
        return TeamMemberAnnouncementDetail
                .builder()
                .teamMemberAnnouncementId(teamMemberAnnouncement.getId())
                .announcementTitle(teamMemberAnnouncement.getAnnouncementTitle())
                .announcementPositionItem(announcementPositionItem)
                .announcementSkillNames(announcementSkillNames)
                .announcementStartDate(teamMemberAnnouncement.getAnnouncementStartDate())
                .announcementEndDate(teamMemberAnnouncement.getAnnouncementEndDate())
                .regionDetail(regionDetail)
                .isRegionFlexible(teamMemberAnnouncement.isRegionFlexible())
                .mainTasks(teamMemberAnnouncement.getMainTasks())
                .workMethod(teamMemberAnnouncement.getWorkMethod())
                .idealCandidate(teamMemberAnnouncement.getIdealCandidate())
                .preferredQualifications(teamMemberAnnouncement.getPreferredQualifications())
                .joiningProcess(teamMemberAnnouncement.getJoiningProcess())
                .benefits(teamMemberAnnouncement.getBenefits())
                .build();
    }

    public TeamMemberAnnouncement toAddTeamMemberAnnouncement(final Team team, final AddTeamMemberAnnouncementRequest request) {
        return TeamMemberAnnouncement
                .builder()
                .team(team)
                .announcementTitle(request.getAnnouncementTitle())
                .announcementStartDate(request.getAnnouncementStartDate())
                .announcementEndDate(request.getAnnouncementEndDate())
                .isRegionFlexible(request.getIsRegionFlexible())
                .mainTasks(request.getMainTasks())
                .workMethod(request.getWorkMethod())
                .idealCandidate(request.getIdealCandidate())
                .preferredQualifications(request.getPreferredQualifications())
                .joiningProcess(request.getJoiningProcess())
                .benefits(request.getBenefits())
                .build();
    }

    public TeamMemberAnnouncementResponseDTO.TeamMemberAnnouncementItems toTeamMemberAnnouncementItems(final List<TeamMemberAnnouncement> teamMemberAnnouncements) {
        List<TeamMemberAnnouncementItem> items = teamMemberAnnouncements.stream()
                .map(this::toTeamMemberAnnouncementItem)
                .collect(Collectors.toList());

        return TeamMemberAnnouncementResponseDTO.TeamMemberAnnouncementItems
                .builder()
                .teamMemberAnnouncementItems(items)
                .build();
    }

    // 수정 필요
    public TeamMemberAnnouncementItem toTeamMemberAnnouncementItem(final TeamMemberAnnouncement teamMemberAnnouncement) {
        return TeamMemberAnnouncementResponseDTO.TeamMemberAnnouncementItem
                .builder()
                .teamMemberAnnouncementId(teamMemberAnnouncement.getId())
                .build();
    }

    public TeamMemberAnnouncementResponseDTO.AnnouncementPositionItem toAnnouncementPositionItem(final AnnouncementPosition announcementPosition) {
        return AnnouncementPositionItem
                .builder()
                .majorPosition(announcementPosition.getPosition().getMajorPosition())
                .subPosition(announcementPosition.getPosition().getSubPosition())
                .build();
    }

    public TeamMemberAnnouncementResponseDTO.AddTeamMemberAnnouncementResponse toAddTeamMemberAnnouncementResponse(
            final TeamMemberAnnouncement teamMemberAnnouncement,
            final AnnouncementPositionItem announcementPositionItem,
            final List<TeamMemberAnnouncementResponseDTO.AnnouncementSkillName> announcementSkillNames,
            final RegionDetail regionDetail
    ) {
        return AddTeamMemberAnnouncementResponse
                .builder()
                .teamMemberAnnouncementId(teamMemberAnnouncement.getId())
                .announcementTitle(teamMemberAnnouncement.getAnnouncementTitle())
                .announcementPositionItem(announcementPositionItem)
                .announcementSkillNames(announcementSkillNames)
                .announcementStartDate(teamMemberAnnouncement.getAnnouncementStartDate())
                .announcementEndDate(teamMemberAnnouncement.getAnnouncementEndDate())
                .cityName(regionDetail.getCityName())
                .divisionName(regionDetail.getDivisionName())
                .isRegionFlexible(teamMemberAnnouncement.isRegionFlexible())
                .mainTasks(teamMemberAnnouncement.getMainTasks())
                .workMethod(teamMemberAnnouncement.getWorkMethod())
                .idealCandidate(teamMemberAnnouncement.getIdealCandidate())
                .preferredQualifications(teamMemberAnnouncement.getPreferredQualifications())
                .joiningProcess(teamMemberAnnouncement.getJoiningProcess())
                .benefits(teamMemberAnnouncement.getBenefits())
                .build();
    }

    public TeamMemberAnnouncementResponseDTO.UpdateTeamMemberAnnouncementResponse toUpdateTeamMemberAnnouncementResponse(
            final TeamMemberAnnouncement teamMemberAnnouncement,
            final AnnouncementPositionItem announcementPositionItem,
            final List<TeamMemberAnnouncementResponseDTO.AnnouncementSkillName> announcementSkillNames,
            final RegionDetail regionDetail
    ) {
        return UpdateTeamMemberAnnouncementResponse
                .builder()
                .teamMemberAnnouncementId(teamMemberAnnouncement.getId())
                .announcementTitle(teamMemberAnnouncement.getAnnouncementTitle())
                .announcementPositionItem(announcementPositionItem)
                .announcementSkillNames(announcementSkillNames)
                .announcementStartDate(teamMemberAnnouncement.getAnnouncementStartDate())
                .announcementEndDate(teamMemberAnnouncement.getAnnouncementEndDate())
                .cityName(regionDetail.getCityName())
                .divisionName(regionDetail.getDivisionName())
                .isRegionFlexible(teamMemberAnnouncement.isRegionFlexible())
                .mainTasks(teamMemberAnnouncement.getMainTasks())
                .workMethod(teamMemberAnnouncement.getWorkMethod())
                .idealCandidate(teamMemberAnnouncement.getIdealCandidate())
                .preferredQualifications(teamMemberAnnouncement.getPreferredQualifications())
                .joiningProcess(teamMemberAnnouncement.getJoiningProcess())
                .benefits(teamMemberAnnouncement.getBenefits())
                .build();
    }

    public TeamMemberAnnouncementResponseDTO.RemoveTeamMemberAnnouncementResponse toRemoveTeamMemberAnnouncementId(final Long teamMemberAnnouncementId) {
        return TeamMemberAnnouncementResponseDTO.RemoveTeamMemberAnnouncementResponse
                .builder()
                .teamMemberAnnouncementId(teamMemberAnnouncementId)
                .build();
    }
    
}
