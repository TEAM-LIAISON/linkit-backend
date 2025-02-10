package liaison.linkit.team.presentation.announcement;

import java.util.Optional;
import liaison.linkit.auth.Auth;
import liaison.linkit.auth.MemberOnly;
import liaison.linkit.auth.domain.Accessor;
import liaison.linkit.common.presentation.CommonResponse;
import liaison.linkit.team.presentation.announcement.dto.TeamMemberAnnouncementRequestDTO;
import liaison.linkit.team.presentation.announcement.dto.TeamMemberAnnouncementResponseDTO;
import liaison.linkit.team.business.service.announcement.TeamMemberAnnouncementService;
import liaison.linkit.team.presentation.announcement.dto.TeamMemberAnnouncementResponseDTO.AnnouncementInformMenus;
import liaison.linkit.team.presentation.announcement.dto.TeamMemberAnnouncementResponseDTO.TeamMemberAnnouncemenItems;
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

    @GetMapping("/home/announcement")
    public CommonResponse<AnnouncementInformMenus> getHomeAnnouncementInformMenus(
        @Auth final Accessor accessor
    ) {
        Optional<Long> optionalMemberId = accessor.isMember() ? Optional.of(accessor.getMemberId()) : Optional.empty();
        return CommonResponse.onSuccess(teamMemberAnnouncementService.getHomeAnnouncementInformMenus(optionalMemberId));
    }

    // 팀원 공고 뷰어 전체 조회
    @GetMapping("/team/{teamCode}/announcement")
    public CommonResponse<TeamMemberAnnouncemenItems> getTeamMemberAnnouncementItems(
        @Auth final Accessor accessor,
        @PathVariable final String teamCode
    ) {
        Optional<Long> optionalMemberId = accessor.isMember() ? Optional.of(accessor.getMemberId()) : Optional.empty();

        return CommonResponse.onSuccess(teamMemberAnnouncementService.getTeamMemberAnnouncementViewItems(optionalMemberId, teamCode));
    }


    /*
    deprecated API
     */
    // 팀원 공고 전체 조회
//    @GetMapping("/team/{teamCode}/announcement/deprecated")
//    @MemberOnly
//    public CommonResponse<TeamMemberAnnouncementResponseDTO.TeamMemberAnnouncementItems> getTeamMemberAnnouncementItems(
//            @Auth final Accessor accessor,
//            @PathVariable final String teamCode
//    ) {
//        log.info("memberId = {}의 teamCode = {}에 대한 팀원 공고 전체 조회 요청이 발생했습니다.", accessor.getMemberId(), teamCode);
//        return CommonResponse.onSuccess(teamMemberAnnouncementService.getTeamMemberAnnouncementItems(accessor.getMemberId(), teamCode));
//    }

    // 팀원 공고 단일 조회
    @GetMapping("/team/{teamCode}/announcement/{teamMemberAnnouncementId}")
    public CommonResponse<TeamMemberAnnouncementResponseDTO.TeamMemberAnnouncementDetail> getTeamMemberAnnouncementDetail(
        @Auth final Accessor accessor,
        @PathVariable final String teamCode,
        @PathVariable final Long teamMemberAnnouncementId
    ) {
        Optional<Long> optionalMemberId = accessor.isMember() ? Optional.of(accessor.getMemberId()) : Optional.empty();

        return CommonResponse.onSuccess(teamMemberAnnouncementService.getTeamMemberAnnouncementDetail(optionalMemberId, teamCode, teamMemberAnnouncementId));

    }

    // 팀원 공고 생성
    @PostMapping("/team/{teamCode}/announcement")
    @MemberOnly
    public CommonResponse<TeamMemberAnnouncementResponseDTO.AddTeamMemberAnnouncementResponse> addTeamMemberAnnouncement(
        @Auth final Accessor accessor,
        @PathVariable final String teamCode,
        @RequestBody final TeamMemberAnnouncementRequestDTO.AddTeamMemberAnnouncementRequest addTeamMemberAnnouncementRequest
    ) {
        log.info("memberId = {}의 teamCode = {}에 대한 팀원 공고 단일 생성 요청이 발생했습니다.", accessor.getMemberId(), teamCode);
        return CommonResponse.onSuccess(teamMemberAnnouncementService.addTeamMemberAnnouncement(accessor.getMemberId(), teamCode, addTeamMemberAnnouncementRequest));
    }

    // 팀원 공고 수정
    @PostMapping("/team/{teamCode}/announcement/{teamMemberAnnouncementId}")
    @MemberOnly
    public CommonResponse<TeamMemberAnnouncementResponseDTO.UpdateTeamMemberAnnouncementResponse> updateTeamMemberAnnouncement(
        @Auth final Accessor accessor,
        @PathVariable final String teamCode,
        @PathVariable final Long teamMemberAnnouncementId,
        @RequestBody final TeamMemberAnnouncementRequestDTO.UpdateTeamMemberAnnouncementRequest updateTeamMemberAnnouncementRequest
    ) {
        log.info("memberId = {}의 teamCode = {}에 대한 팀원 공고 단일 수정 요청이 발생했습니다.", accessor.getMemberId(), teamCode);
        return CommonResponse.onSuccess(teamMemberAnnouncementService.updateTeamMemberAnnouncement(accessor.getMemberId(), teamCode, teamMemberAnnouncementId, updateTeamMemberAnnouncementRequest));
    }

    // 팀원 공고 삭제
    @DeleteMapping("/team/{teamCode}/announcement/{teamMemberAnnouncementId}")
    @MemberOnly
    public CommonResponse<TeamMemberAnnouncementResponseDTO.RemoveTeamMemberAnnouncementResponse> removeTeamMemberAnnouncement(
        @Auth final Accessor accessor,
        @PathVariable final String teamCode,
        @PathVariable final Long teamMemberAnnouncementId
    ) {
        return CommonResponse.onSuccess(teamMemberAnnouncementService.removeTeamMemberAnnouncement(accessor.getMemberId(), teamCode, teamMemberAnnouncementId));
    }

    // 팀원 공고 공개/비공개 여부 수정
    @PostMapping("/team/{teamCode}/announcement/state/{teamMemberAnnouncementId}")
    @MemberOnly
    public CommonResponse<TeamMemberAnnouncementResponseDTO.UpdateTeamMemberAnnouncementPublicStateResponse> updateTeamMemberAnnouncementPublicState(
        @Auth final Accessor accessor,
        @PathVariable final String teamCode,
        @PathVariable final Long teamMemberAnnouncementId
    ) {
        return CommonResponse.onSuccess(teamMemberAnnouncementService.updateTeamMemberAnnouncementPublicState(accessor.getMemberId(), teamCode, teamMemberAnnouncementId));
    }
}
