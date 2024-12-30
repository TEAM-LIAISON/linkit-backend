package liaison.linkit.team.presentation.announcement;

import liaison.linkit.auth.Auth;
import liaison.linkit.auth.MemberOnly;
import liaison.linkit.auth.domain.Accessor;
import liaison.linkit.common.presentation.CommonResponse;
import liaison.linkit.team.presentation.announcement.dto.TeamMemberAnnouncementRequestDTO;
import liaison.linkit.team.presentation.announcement.dto.TeamMemberAnnouncementResponseDTO;
import liaison.linkit.team.service.announcement.TeamMemberAnnouncementService;
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
@RequestMapping("/api/v1/team/{teamName}/announcement")
@Slf4j
public class TeamMemberAnnouncementController {
    private final TeamMemberAnnouncementService teamMemberAnnouncementService;

    // 팀원 공고 뷰어 전체 조회
    @GetMapping("/view")
    public CommonResponse<TeamMemberAnnouncementResponseDTO.TeamMemberAnnouncementViewItems> getTeamMemberAnnouncementViewItems(
            @Auth final Accessor accessor,
            @PathVariable final String teamName
    ) {
        log.info("팀 이름 = {}에 대한 팀원 공고 뷰어 전체 조회 요청이 발생했습니다.", teamName);
        if (accessor.isMember()) {
            final Long memberId = accessor.getMemberId();
            log.info("memberId = {}의 팀원 공고 뷰어 조회 요청이 발생했습니다.", memberId);
            return CommonResponse.onSuccess(teamMemberAnnouncementService.getLoggedInTeamMemberAnnouncementViewItems(memberId, teamName));
        } else {
            log.info("teamName = {}에 팀원 공고 뷰어 조회 요청이 발생했습니다.", teamName);
            return CommonResponse.onSuccess(teamMemberAnnouncementService.getLoggedOutTeamMemberAnnouncementViewItems(teamName));
        }
    }

    // 팀원 공고 전체 조회
    @GetMapping
    @MemberOnly
    public CommonResponse<TeamMemberAnnouncementResponseDTO.TeamMemberAnnouncementItems> getTeamMemberAnnouncementItems(
            @Auth final Accessor accessor,
            @PathVariable final String teamName
    ) {
        log.info("memberId = {}의 팀 이름 = {}에 대한 팀원 공고 전체 조회 요청이 발생했습니다.", accessor.getMemberId(), teamName);
        return CommonResponse.onSuccess(teamMemberAnnouncementService.getTeamMemberAnnouncementItems(accessor.getMemberId(), teamName));
    }

    // 팀원 공고 단일 조회
    @GetMapping("/{teamMemberAnnouncementId}")
    @MemberOnly
    public CommonResponse<TeamMemberAnnouncementResponseDTO.TeamMemberAnnouncementDetail> getTeamMemberAnnouncementDetail(
            @Auth final Accessor accessor,
            @PathVariable final String teamName,
            @PathVariable final Long teamMemberAnnouncementId
    ) {
        log.info("memberId = {}의 팀 이름 = {}에 대한 팀원 공고 상세 조회 요청이 발생했습니다.", accessor.getMemberId(), teamName);
        return CommonResponse.onSuccess(teamMemberAnnouncementService.getTeamMemberAnnouncementDetail(accessor.getMemberId(), teamName, teamMemberAnnouncementId));
    }

    // 팀원 공고 생성
    @PostMapping
    @MemberOnly
    public CommonResponse<TeamMemberAnnouncementResponseDTO.AddTeamMemberAnnouncementResponse> addTeamMemberAnnouncement(
            @Auth final Accessor accessor,
            @PathVariable final String teamName,
            @RequestBody final TeamMemberAnnouncementRequestDTO.AddTeamMemberAnnouncementRequest addTeamMemberAnnouncementRequest
    ) {
        log.info("memberId = {}의 팀 이름 = {}에 대한 팀원 공고 단일 생성 요청이 발생했습니다.", accessor.getMemberId(), teamName);
        return CommonResponse.onSuccess(teamMemberAnnouncementService.addTeamMemberAnnouncement(accessor.getMemberId(), teamName, addTeamMemberAnnouncementRequest));
    }

    // 팀원 공고 수정
    @PostMapping("/{teamMemberAnnouncementId}")
    @MemberOnly
    public CommonResponse<TeamMemberAnnouncementResponseDTO.UpdateTeamMemberAnnouncementResponse> updateTeamMemberAnnouncement(
            @Auth final Accessor accessor,
            @PathVariable final String teamName,
            @PathVariable final Long teamMemberAnnouncementId,
            @RequestBody final TeamMemberAnnouncementRequestDTO.UpdateTeamMemberAnnouncementRequest updateTeamMemberAnnouncementRequest
    ) {
        log.info("memberId = {}의 팀 이름 = {}에 대한 팀원 공고 단일 수정 요청이 발생했습니다.", accessor.getMemberId(), teamName);
        return CommonResponse.onSuccess(teamMemberAnnouncementService.updateTeamMemberAnnouncement(accessor.getMemberId(), teamName, teamMemberAnnouncementId, updateTeamMemberAnnouncementRequest));
    }

    // 팀원 공고 삭제
    @DeleteMapping("/{teamMemberAnnouncementId}")
    @MemberOnly
    public CommonResponse<TeamMemberAnnouncementResponseDTO.RemoveTeamMemberAnnouncementResponse> removeTeamMemberAnnouncement(
            @Auth final Accessor accessor,
            @PathVariable final String teamName,
            @PathVariable final Long teamMemberAnnouncementId
    ) {
        return CommonResponse.onSuccess(teamMemberAnnouncementService.removeTeamMemberAnnouncement(accessor.getMemberId(), teamName, teamMemberAnnouncementId));
    }

    @PostMapping("/state/{teamMemberAnnouncementId}")
    @MemberOnly
    public CommonResponse<TeamMemberAnnouncementResponseDTO.UpdateTeamMemberAnnouncementPublicStateResponse> updateTeamMemberAnnouncementPublicState(
            @Auth final Accessor accessor,
            @PathVariable final String teamName,
            @PathVariable final Long teamMemberAnnouncementId
    ) {
        return CommonResponse.onSuccess(teamMemberAnnouncementService.updateTeamMemberAnnouncementPublicState(accessor.getMemberId(), teamName, teamMemberAnnouncementId));
    }
}
