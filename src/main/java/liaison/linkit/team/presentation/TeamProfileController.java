package liaison.linkit.team.presentation;

import jakarta.validation.Valid;
import liaison.linkit.auth.Auth;
import liaison.linkit.auth.MemberOnly;
import liaison.linkit.auth.domain.Accessor;
import liaison.linkit.team.dto.request.DefaultTeamProfileCreateRequest;
import liaison.linkit.team.dto.response.OnBoardingTeamProfileResponse;
import liaison.linkit.team.dto.response.TeamProfileOnBoardingIsValueResponse;
import liaison.linkit.team.dto.response.TeamProfileTeamBuildingFieldResponse;
import liaison.linkit.team.dto.response.activity.ActivityMethodResponse;
import liaison.linkit.team.dto.response.activity.ActivityRegionResponse;
import liaison.linkit.team.dto.response.activity.ActivityResponse;
import liaison.linkit.team.dto.response.miniProfile.TeamMiniProfileEarlyOnBoardingResponse;
import liaison.linkit.team.dto.response.miniProfile.TeamMiniProfileResponse;
import liaison.linkit.team.dto.response.onBoarding.OnBoardingFirstResponse;
import liaison.linkit.team.service.ActivityService;
import liaison.linkit.team.service.TeamMiniProfileService;
import liaison.linkit.team.service.TeamProfileService;
import liaison.linkit.team.service.TeamProfileTeamBuildingFieldService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/team_profile")
@Slf4j
public class TeamProfileController {

    public final TeamProfileService teamProfileService;
    public final TeamMiniProfileService teamMiniProfileService;
    public final TeamProfileTeamBuildingFieldService teamProfileTeamBuildingFieldService;
    public final ActivityService activityService;


    @GetMapping("/onBoarding")
    @MemberOnly
    public ResponseEntity<OnBoardingTeamProfileResponse> getOnBoardingTeamProfile(
            @Auth final Accessor accessor
    ) {
        // 팀 프로필 아이디 가져오기
        final Long teamProfileId = teamProfileService.validateTeamProfileByMember(accessor.getMemberId());
        log.info("teamProfileId={}", teamProfileId);

        // 저장 여부 파악하는 boolean 가져오기
        final TeamProfileOnBoardingIsValueResponse teamProfileOnBoardingIsValueResponse
                = teamProfileService.getTeamProfileOnBoardingIsValue(teamProfileId);

        // 1. 희망 팀빌딩 분야 및 팀 미니 프로필 내부 항목
        final OnBoardingFirstResponse onBoardingFirstResponse
                = getOnBoardingFirstResponse(accessor.getMemberId(), teamProfileOnBoardingIsValueResponse.isTeamTeamBuildingField());

        log.info("onBoardingFirstResponse={}", onBoardingFirstResponse);

        // 2. 활동 방식 응답
        final ActivityResponse activityResponse
                = getActivityResponse(accessor.getMemberId(), teamProfileOnBoardingIsValueResponse.isActivity());

        log.info("activityResponse={}", activityResponse);

        // 3. 팀원 역할 응답


        // 팀 소개서 미니 프로필 응답
        final TeamMiniProfileResponse teamMiniProfileResponse
                = getTeamMiniProfileResponse(accessor.getMemberId(), teamProfileOnBoardingIsValueResponse.isTeamMiniProfile());

        log.info("teamMiniProfileResponse={}", teamMiniProfileResponse);

        final OnBoardingTeamProfileResponse onBoardingTeamProfileResponse = teamProfileService.getOnBoardingTeamProfile(
                onBoardingFirstResponse,
                activityResponse,
                teamMiniProfileResponse
        );

        return ResponseEntity.ok().body(onBoardingTeamProfileResponse);
    }

    private OnBoardingFirstResponse getOnBoardingFirstResponse(
            final Long memberId,
            final boolean isTeamTeamBuildingField
    ) {
        if (isTeamTeamBuildingField) {
            TeamProfileTeamBuildingFieldResponse teamProfileTeamBuildingFieldResponse
                    = teamProfileTeamBuildingFieldService.getAllTeamProfileTeamBuildingFields(memberId);

            final Long teamMiniProfileId = teamMiniProfileService.validateTeamMiniProfileByMember(memberId);
            // 미니 프로필에서 필요한 것만 찾아서 조회
            TeamMiniProfileEarlyOnBoardingResponse teamMiniProfileEarlyOnBoardingResponse
                    = teamMiniProfileService.getTeamMiniProfileEarlyOnBoarding(teamMiniProfileId);

            return new OnBoardingFirstResponse(teamProfileTeamBuildingFieldResponse, teamMiniProfileEarlyOnBoardingResponse);
        } else {
            return null;
        }
    }

    private ActivityResponse getActivityResponse(
            final Long memberId,
            final boolean isActivity
    ) {
        // ActivityMethod, ActivityRegion 2개 전부 불러와야 함.
        if (isActivity) {
            // 활동 방식
            ActivityMethodResponse activityMethodResponse = activityService.getAllActivityMethods(memberId);
            log.info("activityMethodResponse={}", activityMethodResponse.getActivityTagName());
            // 활동 지역
            ActivityRegionResponse activityRegionResponse = activityService.getActivityRegion(memberId);
            log.info("activityRegionResponse.cityName={}", activityRegionResponse.getCityName());

            return new ActivityResponse(activityMethodResponse, activityRegionResponse);
        } else {
            return null;
        }
    }

    private TeamMiniProfileResponse getTeamMiniProfileResponse(
            final Long memberId,
            final boolean isTeamMiniProfile
    ) {
        if (isTeamMiniProfile) {
            final Long teamMiniProfileId = teamMiniProfileService.validateTeamMiniProfileByMember(memberId);
            return teamMiniProfileService.getTeamMiniProfileDetail(teamMiniProfileId);
        } else {
            return null;
        }
    }

    @PostMapping("/default")
    @MemberOnly
    public ResponseEntity<Void> createDefaultTeamProfile(
            @Auth final Accessor accessor,
            @RequestBody @Valid final DefaultTeamProfileCreateRequest defaultTeamProfileCreateRequest
    ) {
//        teamProfileService.saveDefault(accessor.getMemberId(), defaultTeamProfileCreateRequest);
        return ResponseEntity.ok().build();
    }
}
