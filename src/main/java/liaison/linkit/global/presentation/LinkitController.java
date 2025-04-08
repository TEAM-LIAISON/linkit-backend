package liaison.linkit.global.presentation;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import liaison.linkit.common.presentation.CommonResponse;
import liaison.linkit.global.business.service.LinkitService;
import liaison.linkit.global.config.log.Logging;
import liaison.linkit.global.presentation.dto.LinkitDynamicResponseDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1")
@Slf4j
@Tag(name = "동적 변수 API", description = "링킷 동적 변수 반환 API")
public class LinkitController {

    private final LinkitService linkitService;

    @Operation(summary = "회원 동적 데이터", description = "회원 동적 데이터 조회")
    @ApiResponses({
        @ApiResponse(
                responseCode = "200",
                description = "회원 동적 데이터 조회 성공",
                content =
                        @Content(
                                schema =
                                        @Schema(
                                                implementation =
                                                        LinkitDynamicResponseDTO
                                                                .MemberDynamicListResponse.class)))
    })
    @GetMapping("/linkit/members")
    @Logging(item = "Linkit_Members", action = "GET_LINKIT_MEMBERS_DATA", includeResult = true)
    public CommonResponse<LinkitDynamicResponseDTO.MemberDynamicListResponse>
            getProfileDynamicVariables() {
        return CommonResponse.onSuccess(linkitService.getLinkitProfiles());
    }

    @GetMapping("/linkit/teams")
    @Logging(item = "Linkit_Teams", action = "GET_LINKIT_TEAMS_DATA", includeResult = true)
    public CommonResponse<LinkitDynamicResponseDTO.TeamDynamicListResponse>
            getTeamDynamicVariables() {
        return CommonResponse.onSuccess(linkitService.getLinkitTeams());
    }

    @GetMapping("/linkit/announcements")
    @Logging(
            item = "Linkit_Announcements",
            action = "GET_LINKIT_ANNOUNCEMENTS_DATA",
            includeResult = true)
    public CommonResponse<LinkitDynamicResponseDTO.AnnouncementDynamicListResponse>
            getAnnouncementDynamicVariables() {
        return CommonResponse.onSuccess(linkitService.getLinkitAnnouncements());
    }

    @GetMapping("/linkit/profile/logs")
    @Logging(
            item = "Linkit_Profile_Logs",
            action = "GET_LINKIT_PROFILE_LOGS_DATA",
            includeResult = true)
    public CommonResponse<LinkitDynamicResponseDTO.ProfileLogDynamicListResponse>
            getProfileLogsDynamicVariables() {
        return CommonResponse.onSuccess(linkitService.getLinkitProfileLogs());
    }

    @GetMapping("/linkit/team/logs")
    @Logging(item = "Linkit_Team_Logs", action = "GET_LINKIT_TEAM_LOGS_DATA", includeResult = true)
    public CommonResponse<LinkitDynamicResponseDTO.TeamLogDynamicListResponse>
            getTeamLogsDynamicVariables() {
        return CommonResponse.onSuccess(linkitService.getLinkitTeamLogs());
    }
}
