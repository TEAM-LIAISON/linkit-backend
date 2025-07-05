package liaison.linkit.team.business.mapper.team;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import liaison.linkit.common.annotation.Mapper;
import liaison.linkit.common.presentation.RegionResponseDTO.RegionDetail;
import liaison.linkit.team.domain.team.Team;
import liaison.linkit.team.domain.team.type.TeamStatus;
import liaison.linkit.team.presentation.team.dto.TeamRequestDTO.AddTeamRequest;
import liaison.linkit.team.presentation.team.dto.TeamResponseDTO;
import liaison.linkit.team.presentation.team.dto.TeamResponseDTO.DeleteTeamResponse;
import liaison.linkit.team.presentation.team.dto.TeamResponseDTO.TeamCurrentStateItem;
import liaison.linkit.team.presentation.team.dto.TeamResponseDTO.TeamInformMenu;
import liaison.linkit.team.presentation.team.dto.TeamResponseDTO.TeamInformMenus;
import liaison.linkit.team.presentation.team.dto.TeamResponseDTO.TeamItems;
import liaison.linkit.team.presentation.team.dto.TeamResponseDTO.TeamScaleItem;

@Mapper
public class TeamMapper {

    public Team toTeam(final AddTeamRequest addTeamRequest) {
        return Team.builder()
                .teamName(addTeamRequest.getTeamName())
                .teamCode(addTeamRequest.getTeamCode())
                .teamShortDescription(addTeamRequest.getTeamShortDescription())
                .isTeamPublic(addTeamRequest.getIsTeamPublic())
                .teamStatus(TeamStatus.ACTIVE)
                .build();
    }

    public TeamResponseDTO.AddTeamResponse toAddTeam(
            final Team team,
            final TeamScaleItem teamScaleItem,
            final RegionDetail regionDetail,
            final List<TeamCurrentStateItem> teamCurrentStateItems) {
        return TeamResponseDTO.AddTeamResponse.builder()
                .teamId(team.getId())
                .teamName(team.getTeamName())
                .teamShortDescription(team.getTeamShortDescription())
                .teamScaleItem(teamScaleItem)
                .regionDetail(regionDetail)
                .teamCurrentStates(teamCurrentStateItems)
                .isTeamPublic(team.isTeamPublic())
                .build();
    }

    public TeamResponseDTO.UpdateTeamResponse toUpdateTeam(
            final Team team,
            final TeamScaleItem teamScaleItem,
            final RegionDetail regionDetail,
            final List<TeamCurrentStateItem> teamCurrentStateItems) {
        return TeamResponseDTO.UpdateTeamResponse.builder()
                .teamId(team.getId())
                .teamName(team.getTeamName())
                .teamCode(team.getTeamCode())
                .teamShortDescription(team.getTeamShortDescription())
                .teamScaleItem(teamScaleItem)
                .regionDetail(regionDetail)
                .teamCurrentStates(teamCurrentStateItems)
                .isTeamPublic(team.isTeamPublic())
                .build();
    }

    public TeamResponseDTO.TeamInformMenus toTeamInformMenus(
            final List<TeamInformMenu> teamInformMenus) {
        return TeamInformMenus.builder().teamInformMenus(teamInformMenus).build();
    }

    public TeamResponseDTO.TeamInformMenu toTeamInformMenu(
            final Team team,
            final boolean isTeamScrap,
            final int teamScrapCount,
            final List<TeamCurrentStateItem> teamCurrentStateItems,
            final TeamScaleItem teamScaleItem,
            final RegionDetail regionDetail) {
        return TeamResponseDTO.TeamInformMenu.builder()
                .teamCurrentStates(
                        teamCurrentStateItems != null ? teamCurrentStateItems : new ArrayList<>())
                .isTeamScrap(isTeamScrap)
                .teamScrapCount(teamScrapCount)
                .teamName(team != null && team.getTeamName() != null ? team.getTeamName() : "")
                .teamCode(team != null && team.getTeamCode() != null ? team.getTeamCode() : "")
                .teamShortDescription(
                        team != null && team.getTeamShortDescription() != null
                                ? team.getTeamShortDescription()
                                : "")
                .teamLogoImagePath(
                        team != null && team.getTeamLogoImagePath() != null
                                ? team.getTeamLogoImagePath()
                                : "")
                .teamScaleItem(teamScaleItem != null ? teamScaleItem : new TeamScaleItem())
                .regionDetail(regionDetail != null ? regionDetail : new RegionDetail())
                .build();
    }

    public TeamResponseDTO.TeamDetail toTeamDetail(
            final boolean isMyTeam,
            final boolean isTeamManager,
            final boolean isTeamInvitationInProgress,
            final boolean isTeamDeleteInProgress,
            final boolean isTeamDeleteRequester,
            final TeamInformMenu teamInformMenu,
            final boolean isTeamPublic) {
        return TeamResponseDTO.TeamDetail.builder()
                .isMyTeam(isMyTeam)
                .isTeamManager(isTeamManager)
                .isTeamInvitationInProgress(isTeamInvitationInProgress)
                .isTeamDeleteInProgress(isTeamDeleteInProgress)
                .isTeamDeleteRequester(isTeamDeleteRequester)
                .teamInformMenu(teamInformMenu)
                .isTeamPublic(isTeamPublic)
                .build();
    }

    public TeamResponseDTO.TeamItems toTeamItems(final List<TeamInformMenu> teamInformMenus) {
        return TeamItems.builder().teamInformMenus(teamInformMenus).build();
    }

    public TeamResponseDTO.DeleteTeamResponse toDeleteTeam(
            final String teamCode, final Boolean isTeamLastDeleteRequester) {
        return DeleteTeamResponse.builder()
                .teamCode(teamCode)
                .deletedRequestedAt(LocalDateTime.now())
                .isTeamLastDeleteRequester(isTeamLastDeleteRequester)
                .build();
    }
}
