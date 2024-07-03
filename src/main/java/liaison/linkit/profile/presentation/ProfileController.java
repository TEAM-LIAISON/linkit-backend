package liaison.linkit.profile.presentation;

import jakarta.validation.Valid;
import liaison.linkit.auth.Auth;
import liaison.linkit.auth.MemberOnly;
import liaison.linkit.auth.domain.Accessor;
import liaison.linkit.member.service.MemberService;
import liaison.linkit.profile.dto.request.IntroductionRequest;
import liaison.linkit.profile.dto.response.MemberNameResponse;
import liaison.linkit.profile.dto.response.ProfileIntroductionResponse;
import liaison.linkit.profile.dto.response.ProfileResponse;
import liaison.linkit.profile.dto.response.antecedents.AntecedentsResponse;
import liaison.linkit.profile.dto.response.attach.AttachResponse;
import liaison.linkit.profile.dto.response.awards.AwardsResponse;
import liaison.linkit.profile.dto.response.completion.CompletionResponse;
import liaison.linkit.profile.dto.response.education.EducationResponse;
import liaison.linkit.profile.dto.response.isValue.ProfileIsValueResponse;
import liaison.linkit.profile.dto.response.miniProfile.MiniProfileResponse;
import liaison.linkit.profile.dto.response.onBoarding.JobAndSkillResponse;
import liaison.linkit.profile.dto.response.teamBuilding.ProfileTeamBuildingFieldResponse;
import liaison.linkit.profile.service.*;
import liaison.linkit.profile.service.ProfileOnBoardingService;
import liaison.linkit.profile.dto.response.profileRegion.ProfileRegionResponse;
import liaison.linkit.profile.service.ProfileRegionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;

@RestController
@RequiredArgsConstructor
@RequestMapping("/profile")
@Slf4j
public class ProfileController {

    public final MemberService memberService;
    public final ProfileOnBoardingService profileOnBoardingService;
    public final ProfileService profileService;
    public final MiniProfileService miniProfileService;
    public final CompletionService completionService;
    public final ProfileSkillService profileSkillService;
    public final TeamBuildingFieldService teamBuildingFieldService;
    public final AntecedentsService antecedentsService;
    public final EducationService educationService;
    public final AwardsService awardsService;
    public final AttachService attachService;
    public final ProfileRegionService profileRegionService;

    // 자기소개 생성/수정 메서드
    @PostMapping("/introduction")
    @MemberOnly
    public ResponseEntity<Void> createProfileIntroduction(
            @Auth final Accessor accessor,
            @RequestBody @Valid final IntroductionRequest introductionRequest
    ) {
        profileService.validateProfileByMember(accessor.getMemberId());
        profileService.saveIntroduction(accessor.getMemberId(), introductionRequest);
        return ResponseEntity.ok().build();
    }

    // 내 이력서 전체 조회 GET 메서드
    @GetMapping
    @MemberOnly
    public ResponseEntity<?> getMyProfile(@Auth final Accessor accessor) {
        log.info("내 이력서의 전체 항목 조회 요청 발생");
        try {
            profileService.validateProfileByMember(accessor.getMemberId());
            final ProfileIsValueResponse profileIsValueResponse = profileService.getProfileIsValue(accessor.getMemberId());
            final MiniProfileResponse miniProfileResponse = getMiniProfileResponse(accessor.getMemberId(), profileIsValueResponse.isMiniProfile());
            final MemberNameResponse memberNameResponse = getMemberNameResponse(accessor.getMemberId());
            final CompletionResponse completionResponse = getCompletionResponse(accessor.getMemberId());
            final ProfileIntroductionResponse profileIntroductionResponse = getProfileIntroduction(accessor.getMemberId(), profileIsValueResponse.isIntroduction());
            final JobAndSkillResponse jobAndSkillResponse = getJobAndSkillResponse(accessor.getMemberId(), profileIsValueResponse.isJobAndSkill());
            final ProfileTeamBuildingFieldResponse profileTeamBuildingFieldResponse = getProfileTeamBuildingResponse(accessor.getMemberId(), profileIsValueResponse.isProfileTeamBuildingField());
            final ProfileRegionResponse profileRegionResponse = getProfileRegionResponse(accessor.getMemberId(), profileIsValueResponse.isProfileRegion());
            final List<AntecedentsResponse> antecedentsResponses = getAntecedentsResponses(accessor.getMemberId(), profileIsValueResponse.isAntecedents());
            final List<EducationResponse> educationResponses = getEducationResponses(accessor.getMemberId(), profileIsValueResponse.isEducation());
            final List<AwardsResponse> awardsResponses = getAwardsResponses(accessor.getMemberId(), profileIsValueResponse.isAwards());
            final AttachResponse attachResponse = getAttachResponses(accessor.getMemberId(), profileIsValueResponse.isAttach());

            final ProfileResponse profileResponse = profileService.getProfileResponse(
                    miniProfileResponse,
                    memberNameResponse,
                    completionResponse,
                    profileIntroductionResponse,
                    jobAndSkillResponse,
                    profileTeamBuildingFieldResponse,
                    profileRegionResponse,
                    antecedentsResponses,
                    educationResponses,
                    awardsResponses,
                    attachResponse
            );
            return ResponseEntity.ok().body(profileResponse);
        } catch (Exception e) {
            log.error("내 이력서 조회 과정에서 예외 발생: {}", e.getMessage());
            return ResponseEntity.status(INTERNAL_SERVER_ERROR).body("내 이력서 정보를 불러오는 과정에서 문제가 발생했습니다.");
        }
    }

    private MiniProfileResponse getMiniProfileResponse(
            final Long memberId,
            final boolean isMiniProfile
    ) {
        if (isMiniProfile) {
            miniProfileService.validateMiniProfileByMember(memberId);
            return miniProfileService.getPersonalMiniProfile(memberId);
        } else {
            return new MiniProfileResponse();
        }
    }

    private MemberNameResponse getMemberNameResponse(
            final Long memberId
    ) {
        return memberService.getMemberName(memberId);
    }

    private CompletionResponse getCompletionResponse(
            final Long memberId
    ) {
        return completionService.getCompletion(memberId);
    }

    private ProfileIntroductionResponse getProfileIntroduction(
            final Long memberId,
            final boolean isIntroduction
    ) {
        if (isIntroduction) {
            return profileService.getProfileIntroduction(memberId);
        } else {
            return new ProfileIntroductionResponse();
        }
    }

    private ProfileTeamBuildingFieldResponse getProfileTeamBuildingResponse(
            final Long memberId,
            final boolean isProfileTeamBuildingField
    ) {
        if (isProfileTeamBuildingField) {
            teamBuildingFieldService.validateProfileTeamBuildingFieldByMember(memberId);
            return teamBuildingFieldService.getAllProfileTeamBuildingFields(memberId);
        } else {
            return new ProfileTeamBuildingFieldResponse();
        }
    }

    private ProfileRegionResponse getProfileRegionResponse(
            final Long memberId,
            final boolean isProfileRegion
    ) {
        if (isProfileRegion) {
            profileRegionService.validateProfileRegionByMember(memberId);
            return profileRegionService.getPersonalProfileRegion(memberId);
        } else {
            return null;
        }
    }

    // 1.5.4. 역할 및 보유 기술 조회
    private JobAndSkillResponse getJobAndSkillResponse(
            final Long memberId,
            final boolean isJobAndSkill
    ) {
        if (isJobAndSkill) {
            profileOnBoardingService.validateProfileByMember(memberId);
            return profileOnBoardingService.getJobAndSkill(memberId);
        } else {
            return new JobAndSkillResponse();
        }
    }

    private List<EducationResponse> getEducationResponses(
            final Long memberId,
            final boolean isEducation
    ) {
        if (isEducation) {
            educationService.validateEducationByMember(memberId);
            return educationService.getAllEducations(memberId);
        } else {
            return null;
        }
    }

    private List<AwardsResponse> getAwardsResponses(
            final Long memberId,
            final boolean isAwards
    ) {
        if (isAwards) {
            awardsService.validateAwardsByMember(memberId);
            return awardsService.getAllAwards(memberId);
        } else {
            return null;
        }
    }

    private List<AntecedentsResponse> getAntecedentsResponses(
            final Long memberId,
            final boolean isAntecedents
    ) {
        if (isAntecedents) {
            antecedentsService.validateAntecedentsByMember(memberId);
            return antecedentsService.getAllAntecedents(memberId);
        } else {
            return null;
        }
    }

    private AttachResponse getAttachResponses(
            final Long memberId,
            final boolean isAttach
    ) {
        if (isAttach) {
            return attachService.getAttachList(memberId);
        } else {
            return new AttachResponse();
        }
    }
}
