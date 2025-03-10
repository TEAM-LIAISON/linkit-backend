package liaison.linkit.team.presentation.announcement;

import java.util.Optional;

import liaison.linkit.auth.Auth;
import liaison.linkit.auth.MemberOnly;
import liaison.linkit.auth.domain.Accessor;
import liaison.linkit.common.presentation.CommonResponse;
import liaison.linkit.global.config.log.Logging;
import liaison.linkit.team.business.service.announcement.TeamMemberAnnouncementService;
import liaison.linkit.team.presentation.announcement.dto.TeamMemberAnnouncementRequestDTO;
import liaison.linkit.team.presentation.announcement.dto.TeamMemberAnnouncementResponseDTO;
import liaison.linkit.team.presentation.announcement.dto.TeamMemberAnnouncementResponseDTO.AnnouncementInformMenus;
import liaison.linkit.team.presentation.announcement.dto.TeamMemberAnnouncementResponseDTO.TeamMemberAnnouncementItems;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

// 팀원 공고
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1")
@Slf4j
public class TeamMemberAnnouncementController {

    private final TeamMemberAnnouncementService teamMemberAnnouncementService;

    // 홈화면 모집 공고 조회
    @GetMapping("/home/announcement")
    @Logging(
            item = "Team_Member_Announcement",
            action = "GET_HOME_ANNOUNCEMENT_INFORM_MENUS",
            includeResult = false)
    public CommonResponse<AnnouncementInformMenus> getHomeAnnouncementInformMenus(
            @Auth final Accessor accessor) {
        ;
        Optional<Long> optionalMemberId =
                accessor.isMember() ? Optional.of(accessor.getMemberId()) : Optional.empty();
        return CommonResponse.onSuccess(
                teamMemberAnnouncementService.getHomeAnnouncementInformMenus(optionalMemberId));
    }

    // 팀원 공고 뷰어 전체 조회
    @GetMapping("/team/{teamCode}/announcement")
    @Logging(
            item = "Team_Member_Announcement",
            action = "GET_TEAM_MEMBER_ANNOUNCEMENT_ITEMS",
            includeResult = true)
    public CommonResponse<TeamMemberAnnouncementItems> getTeamMemberAnnouncementItems(
            @Auth final Accessor accessor, @PathVariable final String teamCode) {
        Optional<Long> optionalMemberId =
                accessor.isMember() ? Optional.of(accessor.getMemberId()) : Optional.empty();

        return CommonResponse.onSuccess(
                teamMemberAnnouncementService.getTeamMemberAnnouncementViewItems(
                        optionalMemberId, teamCode));
    }

    // 팀원 공고 단일 조회
    @GetMapping("/team/{teamCode}/announcement/{teamMemberAnnouncementId}")
    @Logging(
            item = "Team_Member_Announcement",
            action = "GET_TEAM_MEMBER_ANNOUNCEMENT_DETAIL",
            includeResult = true)
    public CommonResponse<TeamMemberAnnouncementResponseDTO.TeamMemberAnnouncementDetail>
            getTeamMemberAnnouncementDetail(
                    @Auth final Accessor accessor,
                    @PathVariable final String teamCode,
                    @PathVariable final Long teamMemberAnnouncementId) {
        Optional<Long> optionalMemberId =
                accessor.isMember() ? Optional.of(accessor.getMemberId()) : Optional.empty();

        return CommonResponse.onSuccess(
                teamMemberAnnouncementService.getTeamMemberAnnouncementDetail(
                        optionalMemberId, teamCode, teamMemberAnnouncementId));
    }

    // 팀원 공고 생성
    @PostMapping("/team/{teamCode}/announcement")
    @MemberOnly
    @Logging(
            item = "Team_Member_Announcement",
            action = "POST_ADD_TEAM_MEMBER_ANNOUNCEMENT",
            includeResult = true)
    public CommonResponse<TeamMemberAnnouncementResponseDTO.AddTeamMemberAnnouncementResponse>
            addTeamMemberAnnouncement(
                    @Auth final Accessor accessor,
                    @PathVariable final String teamCode,
                    @RequestBody
                            final TeamMemberAnnouncementRequestDTO.AddTeamMemberAnnouncementRequest
                                    addTeamMemberAnnouncementRequest) {
        return CommonResponse.onSuccess(
                teamMemberAnnouncementService.addTeamMemberAnnouncement(
                        accessor.getMemberId(), teamCode, addTeamMemberAnnouncementRequest));
    }

    // 팀원 공고 수정
    @PostMapping("/team/{teamCode}/announcement/{teamMemberAnnouncementId}")
    @MemberOnly
    @Logging(
            item = "Team_Member_Announcement",
            action = "POST_UPDATE_TEAM_MEMBER_ANNOUNCEMENT",
            includeResult = true)
    public CommonResponse<TeamMemberAnnouncementResponseDTO.UpdateTeamMemberAnnouncementResponse>
            updateTeamMemberAnnouncement(
                    @Auth final Accessor accessor,
                    @PathVariable final String teamCode,
                    @PathVariable final Long teamMemberAnnouncementId,
                    @RequestBody
                            final TeamMemberAnnouncementRequestDTO
                                            .UpdateTeamMemberAnnouncementRequest
                                    updateTeamMemberAnnouncementRequest) {
        return CommonResponse.onSuccess(
                teamMemberAnnouncementService.updateTeamMemberAnnouncement(
                        accessor.getMemberId(),
                        teamCode,
                        teamMemberAnnouncementId,
                        updateTeamMemberAnnouncementRequest));
    }

    // 팀원 공고 삭제
    @DeleteMapping("/team/{teamCode}/announcement/{teamMemberAnnouncementId}")
    @MemberOnly
    @Logging(
            item = "Team_Member_Announcement",
            action = "DELETE_REMOVE_TEAM_MEMBER_ANNOUNCEMENT",
            includeResult = true)
    public CommonResponse<TeamMemberAnnouncementResponseDTO.RemoveTeamMemberAnnouncementResponse>
            removeTeamMemberAnnouncement(
                    @Auth final Accessor accessor,
                    @PathVariable final String teamCode,
                    @PathVariable final Long teamMemberAnnouncementId) {
        return CommonResponse.onSuccess(
                teamMemberAnnouncementService.removeTeamMemberAnnouncement(
                        accessor.getMemberId(), teamCode, teamMemberAnnouncementId));
    }

    // 팀원 공고 공개/비공개 여부 수정
    @PostMapping("/team/{teamCode}/announcement/state/{teamMemberAnnouncementId}")
    @MemberOnly
    @Logging(
            item = "Team_Member_Announcement",
            action = "POST_UPDATE_TEAM_MEMBER_ANNOUNCEMENT_PUBLIC_STATE",
            includeResult = true)
    public CommonResponse<
                    TeamMemberAnnouncementResponseDTO
                            .UpdateTeamMemberAnnouncementPublicStateResponse>
            updateTeamMemberAnnouncementPublicState(
                    @Auth final Accessor accessor,
                    @PathVariable final String teamCode,
                    @PathVariable final Long teamMemberAnnouncementId) {
        return CommonResponse.onSuccess(
                teamMemberAnnouncementService.updateTeamMemberAnnouncementPublicState(
                        accessor.getMemberId(), teamCode, teamMemberAnnouncementId));
    }

    // 모집 중 공고를 모집 마감으로 변경
    @PostMapping("/team/{teamCode}/announcement/close/{teamMemberAnnouncementId}")
    @MemberOnly
    @Logging(
            item = "Team_Member_Announcement",
            action = "POST_CLOSE_TEAM_MEMBER_ANNOUNCEMENT",
            includeResult = true)
    public CommonResponse<TeamMemberAnnouncementResponseDTO.CloseTeamMemberAnnouncementResponse>
            closeTeamMemberAnnouncement(
                    @Auth final Accessor accessor,
                    @PathVariable final String teamCode,
                    @PathVariable final Long teamMemberAnnouncementId) {
        return CommonResponse.onSuccess(
                teamMemberAnnouncementService.closeTeamMemberAnnouncement(
                        accessor.getMemberId(), teamCode, teamMemberAnnouncementId));
    }
}
