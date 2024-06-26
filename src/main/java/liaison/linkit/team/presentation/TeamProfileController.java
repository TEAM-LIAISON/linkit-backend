package liaison.linkit.team.presentation;

import jakarta.validation.Valid;
import liaison.linkit.auth.Auth;
import liaison.linkit.auth.MemberOnly;
import liaison.linkit.auth.domain.Accessor;
import liaison.linkit.team.dto.request.TeamIntroductionCreateRequest;
import liaison.linkit.team.dto.request.onBoarding.OnBoardingFieldTeamInformRequest;
import liaison.linkit.team.dto.response.*;
import liaison.linkit.team.dto.response.activity.ActivityResponse;
import liaison.linkit.team.dto.response.announcement.TeamMemberAnnouncementResponse;
import liaison.linkit.team.dto.response.attach.TeamAttachResponse;
import liaison.linkit.team.dto.response.completion.TeamCompletionResponse;
import liaison.linkit.team.dto.response.history.HistoryResponse;
import liaison.linkit.team.dto.response.miniProfile.TeamMiniProfileResponse;
import liaison.linkit.team.dto.response.onBoarding.OnBoardingFieldTeamInformResponse;
import liaison.linkit.team.service.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;

@RestController
@RequiredArgsConstructor
@RequestMapping("/team_profile")
@Slf4j
public class TeamProfileController {

    final TeamProfileService teamProfileService;
    // 4.1.
    final TeamMiniProfileService teamMiniProfileService;
    // 4.3.
    final TeamCompletionService teamCompletionService;
    // 4.4.
    final TeamProfileTeamBuildingFieldService teamProfileTeamBuildingFieldService;
    // 4.5.
    final TeamMemberAnnouncementService teamMemberAnnouncementService;
    // 4.6.
    final ActivityService activityService;
    // 4.8.
    final TeamMemberIntroductionService teamMemberIntroductionService;
    // 4.9.
    final HistoryService historyService;
    // 4.10.
    final TeamAttachService teamAttachService;


    // 팀 소개서 전체 조회
    @GetMapping
    @MemberOnly
    public ResponseEntity<?> getTeamProfile(
            @Auth final Accessor accessor
    ) {
        try {
            log.info("--- 팀 이력서 조회 요청이 들어왔습니다. ---");
            teamProfileService.validateTeamProfileByMember(accessor.getMemberId());

            // 팀 소개서에 있는 항목들의 존재 여부 파악
            final TeamProfileIsValueResponse teamProfileIsValueResponse
                    = teamProfileService.getTeamProfileIsValue(accessor.getMemberId());

            // 4.1. 팀 미니 프로필
            final TeamMiniProfileResponse teamMiniProfileResponse
                    = getTeamMiniProfileResponse(accessor.getMemberId(), teamProfileIsValueResponse.isTeamMiniProfile());
            log.info("teamMiniProfileResponse={}", teamMiniProfileResponse);

            // 4.2. 매칭 추천

            // 4.3. 프로필 완성도
            final TeamCompletionResponse teamCompletionResponse
                    = getTeamCompletionResponse(accessor.getMemberId());

            // 4.4. 희망 팀빌딩 분야
            final TeamProfileTeamBuildingFieldResponse teamProfileTeamBuildingFieldResponse
                    = getTeamProfileTeamBuildingFieldResponse(accessor.getMemberId(), teamProfileIsValueResponse.isTeamProfileTeamBuildingField());

            // 4.5. 팀원 공고
            final List<TeamMemberAnnouncementResponse> teamMemberAnnouncementResponse
                    = getTeamMemberAnnouncement(accessor.getMemberId(), teamProfileIsValueResponse.isTeamMemberAnnouncement());

            // 4.6. 활동 방식 + 활동 지역/위치
            final ActivityResponse activityResponse
                    = getActivityResponse(accessor.getMemberId(), teamProfileIsValueResponse.isActivity());

            // 4.7. 팀 소개
            final TeamProfileIntroductionResponse teamProfileIntroductionResponse
                    = getTeamProfileIntroduction(accessor.getMemberId(), teamProfileIsValueResponse.isTeamIntroduction());

            // 4.8. 팀원 소개
            final List<TeamMemberIntroductionResponse> teamMemberIntroductionResponse
                    = getTeamMemberIntroduction(accessor.getMemberId(), teamProfileIsValueResponse.isTeamMemberIntroduction());

            // 4.9. 연혁
            final List<HistoryResponse> historyResponse
                    = getHistory(accessor.getMemberId(), teamProfileIsValueResponse.isHistory());

            // 4.10. 첨부
            final TeamAttachResponse teamAttachResponse
                    = getTeamAttach(accessor.getMemberId(), teamProfileIsValueResponse.isTeamAttach());
            log.info("teamAttachResponse={}", teamAttachResponse);

            final TeamProfileResponse teamProfileResponse = teamProfileService.getTeamProfileResponse(
                    teamMiniProfileResponse,
                    teamCompletionResponse,
                    teamProfileTeamBuildingFieldResponse,
                    teamMemberAnnouncementResponse,
                    activityResponse,
                    teamProfileIntroductionResponse,
                    teamMemberIntroductionResponse,
                    historyResponse,
                    teamAttachResponse
            );

            return ResponseEntity.ok().body(teamProfileResponse);
        } catch (Exception e) {
            return ResponseEntity.status(INTERNAL_SERVER_ERROR).body("팀 소개력서 정보를 불러오는 과정에서 문제가 발생했습니다.");
        }

    }



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



    // --- 전체 조회 관련 메서드 ---

    // 4.3. 프로필 완성도
    private TeamCompletionResponse getTeamCompletionResponse(
            final Long memberId
    ) {
        return teamCompletionService.getTeamCompletion(memberId);
    }

    // 4.4. 희망 팀빌딩 분야
    private TeamProfileTeamBuildingFieldResponse getTeamProfileTeamBuildingFieldResponse(
            final Long memberId,
            final boolean isTeamProfileTeamBuildingField
    ) {
        // true case
        if (isTeamProfileTeamBuildingField) {
            return teamProfileTeamBuildingFieldService.getAllTeamProfileTeamBuildingFields(memberId);
        } else {
            return new TeamProfileTeamBuildingFieldResponse();
        }

    }

    // 4.5. 팀원 공고
    private List<TeamMemberAnnouncementResponse> getTeamMemberAnnouncement(
            final Long memberId,
            final boolean isTeamMemberAnnouncement
    ) {
        if (isTeamMemberAnnouncement) {
            return teamMemberAnnouncementService.getTeamMemberAnnouncement(memberId);
        } else {
            return null;
        }
    }
    // 4.6. 활동 방식 + 활동 지역 및 위치
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

    // 4.7. 팀 소개
    private TeamProfileIntroductionResponse getTeamProfileIntroduction(
            final Long memberId,
            final boolean isTeamIntroduction
    ) {
        if (isTeamIntroduction) {
            return teamProfileService.getTeamIntroduction(memberId);
        } else {
            return new TeamProfileIntroductionResponse();
        }
    }

    // 4.8. 팀원 소개
    private List<TeamMemberIntroductionResponse> getTeamMemberIntroduction(
            final Long memberId,
            final boolean isTeamMemberIntroduction
    ) {
        if (isTeamMemberIntroduction) {
            return teamMemberIntroductionService.getAllTeamMemberIntroduction(memberId);
        } else {
            return null;
        }
    }

    // 4.9. 연혁
    private List<HistoryResponse> getHistory(
            final Long memberId,
            final boolean isHistory
    ) {
        if (isHistory) {
            return historyService.getAllHistories(memberId);
        } else {
            return null;
        }
    }

    // 4.10 첨부
    private TeamAttachResponse getTeamAttach(
            final Long memberId,
            final boolean isTeamAttach
    ) {
        if (isTeamAttach) {
            return teamAttachService.getTeamAttachList(memberId);
        } else {
            return new TeamAttachResponse();
        }
    }
}
