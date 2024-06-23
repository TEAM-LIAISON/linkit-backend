package liaison.linkit.team.presentation;

import jakarta.validation.Valid;
import liaison.linkit.auth.Auth;
import liaison.linkit.auth.MemberOnly;
import liaison.linkit.auth.domain.Accessor;
import liaison.linkit.team.dto.request.TeamIntroductionCreateRequest;
import liaison.linkit.team.dto.request.onBoarding.OnBoardingFieldTeamInformRequest;
import liaison.linkit.team.dto.response.OnBoardingTeamProfileResponse;
import liaison.linkit.team.dto.response.TeamProfileOnBoardingIsValueResponse;
import liaison.linkit.team.dto.response.activity.ActivityResponse;
import liaison.linkit.team.dto.response.miniProfile.TeamMiniProfileResponse;
import liaison.linkit.team.dto.response.onBoarding.OnBoardingFieldTeamInformResponse;
import liaison.linkit.team.service.ActivityService;
import liaison.linkit.team.service.TeamMiniProfileService;
import liaison.linkit.team.service.TeamProfileService;
import liaison.linkit.team.service.TeamProfileTeamBuildingFieldService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;

@RestController
@RequiredArgsConstructor
@RequestMapping("/team_profile")
@Slf4j
public class TeamProfileController {

    final TeamProfileService teamProfileService;
    final TeamProfileTeamBuildingFieldService teamProfileTeamBuildingFieldService;
    final TeamMiniProfileService teamMiniProfileService;
    final ActivityService activityService;


    // 팀 소개서 전체 조회
//    @GetMapping
//    @MemberOnly
//    public ResponseEntity<?> getTeamProfile(@Auth final Accessor accessor) {
//        try{
//            log.info("--- 팀 이력서 조회 요청이 들어왔습니다. ---");
//            teamProfileService.validateTeamProfileByMember(accessor.getMemberId());
//
//            // 팀 소개서에 있는 항목들의 존재 여부 파악
//            final TeamProfileIsValueResponse teamProfileIsValueResponse
//                    = teamProfileService.getTeamProfileIsValue(accessor.getMemberId());
//        }
//    }


    @GetMapping("/onBoarding")
    @MemberOnly
    public ResponseEntity<?> getOnBoardingTeamProfile(@Auth final Accessor accessor) {
        try {
            log.info("--- 팀 소개서 온보딩 조회 요청이 들어왔습니다 ---");
            teamProfileService.validateTeamProfileByMember(accessor.getMemberId());

            final TeamProfileOnBoardingIsValueResponse teamProfileOnBoardingIsValueResponse
                    = teamProfileService.getTeamProfileOnBoardingIsValue(accessor.getMemberId());
            log.info("teamProfileOnBoardingIsValueResponse={}", teamProfileOnBoardingIsValueResponse);

            final OnBoardingFieldTeamInformResponse onBoardingFieldTeamInformResponse
                    = getOnBoardingFieldTeamInformResponse(accessor.getMemberId(), teamProfileOnBoardingIsValueResponse.isTeamProfileTeamBuildingField(), teamProfileOnBoardingIsValueResponse.isTeamMiniProfile());
            log.info("onBoardingFieldTeamInformResponse={}", onBoardingFieldTeamInformResponse);

            log.info("활동 방식 및 지역 오류 확인 범위 시작 부분");
            final ActivityResponse activityResponse
                    = getActivityResponse(accessor.getMemberId(), teamProfileOnBoardingIsValueResponse.isActivity());
            log.info("activityResponse={}", activityResponse);

            final TeamMiniProfileResponse teamMiniProfileResponse
                    = getTeamMiniProfileResponse(accessor.getMemberId(), teamProfileOnBoardingIsValueResponse.isTeamMiniProfile());
            log.info("teamMiniProfileResponse={}", teamMiniProfileResponse);

            final OnBoardingTeamProfileResponse onBoardingTeamProfileResponse = new OnBoardingTeamProfileResponse(
                    onBoardingFieldTeamInformResponse,
                    activityResponse,
                    teamMiniProfileResponse
            );

            log.info("onBoardingTeamProfileResponse={}", onBoardingTeamProfileResponse);

            return ResponseEntity.ok().body(onBoardingTeamProfileResponse);

        } catch (Exception e) {
            log.error("온보딩 조회 과정에서 예외 발생: {}", e.getMessage());
            return ResponseEntity.status(INTERNAL_SERVER_ERROR).body("온보딩 정보를 불러오는 과정에서 문제가 발생했습니다.");
        }

    }

    private TeamMiniProfileResponse getTeamMiniProfileResponse(
            final Long memberId,
            final boolean isTeamMiniProfile
    ) {
        if (isTeamMiniProfile) {
            teamMiniProfileService.validateTeamMiniProfileByMember(memberId);
            return teamMiniProfileService.getPersonalTeamMiniProfile(memberId);
        } else {
            return new TeamMiniProfileResponse();
        }
    }

    private ActivityResponse getActivityResponse(
            final Long memberId,
            final boolean isActivity
    ) {
        log.info("getActivityResponse() 메서드 실행 여부");
        if (isActivity) {
            log.info("isActivity={}", isActivity);
            return activityService.getActivity(memberId);
        } else {
            log.info("isActivity={}", isActivity);
            return new ActivityResponse();
        }
    }

    private OnBoardingFieldTeamInformResponse getOnBoardingFieldTeamInformResponse(
            final Long memberId,
            final boolean isTeamProfileTeamBuildingField,
            final boolean isTeamMiniProfile
    ) {
        if (isTeamProfileTeamBuildingField && isTeamMiniProfile) {
            teamProfileTeamBuildingFieldService.validateTeamProfileTeamBuildingFieldByMember(memberId);
            teamMiniProfileService.validateTeamMiniProfileByMember(memberId);

            return new OnBoardingFieldTeamInformResponse(
                    teamProfileTeamBuildingFieldService.getAllTeamProfileTeamBuildingFields(memberId),
                    teamMiniProfileService.getTeamMiniProfileEarlyOnBoarding(memberId)
            );
        } else {
            return new OnBoardingFieldTeamInformResponse();
        }
    }

    // 팀 소개서 온보딩 과정에서 첫번째 항목
    @PostMapping("/field/basic-team")
    @MemberOnly
    public ResponseEntity<Void> createOnBoardingFirst(
            @Auth final Accessor accessor,
            @RequestBody @Valid final OnBoardingFieldTeamInformRequest onBoardingFieldTeamInformRequest
    ) {
        // 일단 희망 팀빌딩 분야부터 처리
        teamProfileTeamBuildingFieldService.saveTeamBuildingField(accessor.getMemberId(), onBoardingFieldTeamInformRequest.getTeamBuildingFieldNames());

        // 미니 프로필에 있는 팀 제목, 규모, 분야 저장
        teamMiniProfileService.saveOnBoarding(accessor.getMemberId(), onBoardingFieldTeamInformRequest);

        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @PostMapping("/introduction")
    @MemberOnly
    public ResponseEntity<Void> createTeamIntroduction(
            @Auth final Accessor accessor,
            @RequestBody @Valid final TeamIntroductionCreateRequest teamIntroductionCreateRequest
    ) {
        teamProfileService.validateTeamProfileByMember(accessor.getMemberId());
        teamProfileService.saveTeamIntroduction(accessor.getMemberId(), teamIntroductionCreateRequest);
        return ResponseEntity.ok().build();
    }

}
