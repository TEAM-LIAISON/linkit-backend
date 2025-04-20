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
import liaison.linkit.global.presentation.dto.HomeResponseDTO;
import liaison.linkit.global.presentation.dto.LinkitDynamicResponseDTO.AnnouncementDynamicListResponse;
import liaison.linkit.global.presentation.dto.LinkitDynamicResponseDTO.MemberDynamicListResponse;
import liaison.linkit.global.presentation.dto.LinkitDynamicResponseDTO.ProfileLogDynamicListResponse;
import liaison.linkit.global.presentation.dto.LinkitDynamicResponseDTO.TeamDynamicListResponse;
import liaison.linkit.global.presentation.dto.LinkitDynamicResponseDTO.TeamLogDynamicListResponse;
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

    @GetMapping("/home")
    public CommonResponse<HomeResponseDTO.HomeResponse> getHome() {
        return CommonResponse.onSuccess(linkitService.getHome());
    }

    @Operation(summary = "회원 동적 데이터", description = "회원 동적 데이터 조회")
    @ApiResponses({
        @ApiResponse(
                responseCode = "200",
                description = "회원 동적 데이터 조회 성공",
                content =
                        @Content(
                                schema = @Schema(implementation = MemberDynamicListResponse.class)))
    })
    @GetMapping("/linkit/members")
    @Logging(item = "Linkit_Members", action = "GET_LINKIT_MEMBERS_DATA", includeResult = true)
    public CommonResponse<MemberDynamicListResponse> getProfileDynamicVariables() {
        return CommonResponse.onSuccess(linkitService.getLinkitProfiles());
    }

    @Operation(summary = "팀 동적 데이터", description = "팀 동적 데이터 조회")
    @ApiResponses({
        @ApiResponse(
                responseCode = "200",
                description = "팀 동적 데이터 조회 성공",
                content =
                        @Content(schema = @Schema(implementation = TeamDynamicListResponse.class)))
    })
    @GetMapping("/linkit/teams")
    @Logging(item = "Linkit_Teams", action = "GET_LINKIT_TEAMS_DATA", includeResult = true)
    public CommonResponse<TeamDynamicListResponse> getTeamDynamicVariables() {
        return CommonResponse.onSuccess(linkitService.getLinkitTeams());
    }

    @Operation(summary = "공고 동적 데이터", description = "공고 동적 데이터 조회")
    @ApiResponses({
        @ApiResponse(
                responseCode = "200",
                description = "공고 동적 데이터 조회 성공",
                content =
                        @Content(
                                schema =
                                        @Schema(
                                                implementation =
                                                        AnnouncementDynamicListResponse.class)))
    })
    @GetMapping("/linkit/announcements")
    @Logging(
            item = "Linkit_Announcements",
            action = "GET_LINKIT_ANNOUNCEMENTS_DATA",
            includeResult = true)
    public CommonResponse<AnnouncementDynamicListResponse> getAnnouncementDynamicVariables() {
        return CommonResponse.onSuccess(linkitService.getLinkitAnnouncements());
    }

    @Operation(summary = "프로필 로그 동적 데이터", description = "프로필 로그 동적 데이터 조회")
    @ApiResponses({
        @ApiResponse(
                responseCode = "200",
                description = "프로필 로그 동적 데이터 조회 성공",
                content =
                        @Content(
                                schema =
                                        @Schema(
                                                implementation =
                                                        ProfileLogDynamicListResponse.class)))
    })
    @GetMapping("/linkit/profile/logs")
    @Logging(
            item = "Linkit_Profile_Logs",
            action = "GET_LINKIT_PROFILE_LOGS_DATA",
            includeResult = true)
    public CommonResponse<ProfileLogDynamicListResponse> getProfileLogsDynamicVariables() {
        return CommonResponse.onSuccess(linkitService.getLinkitProfileLogs());
    }

    @Operation(summary = "팀 로그 동적 데이터", description = "팀 로그 동적 데이터 조회")
    @ApiResponses({
        @ApiResponse(
                responseCode = "200",
                description = "팀 로그 동적 데이터 조회 성공",
                content =
                        @Content(
                                schema =
                                        @Schema(implementation = TeamLogDynamicListResponse.class)))
    })
    @GetMapping("/linkit/team/logs")
    @Logging(item = "Linkit_Team_Logs", action = "GET_LINKIT_TEAM_LOGS_DATA", includeResult = true)
    public CommonResponse<TeamLogDynamicListResponse> getTeamLogsDynamicVariables() {
        return CommonResponse.onSuccess(linkitService.getLinkitTeamLogs());
    }
}
