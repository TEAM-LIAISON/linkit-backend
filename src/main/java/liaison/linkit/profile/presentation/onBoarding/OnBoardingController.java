package liaison.linkit.profile.presentation.onBoarding;

import jakarta.validation.Valid;
import liaison.linkit.auth.Auth;
import liaison.linkit.auth.MemberOnly;
import liaison.linkit.auth.domain.Accessor;
import liaison.linkit.member.service.MemberService;
import liaison.linkit.profile.dto.request.onBoarding.personal.OnBoardingPersonalJobAndSkillCreateRequest;
import liaison.linkit.profile.dto.response.MemberNameResponse;
import liaison.linkit.profile.dto.response.OnBoardingProfileResponse;
import liaison.linkit.profile.dto.response.antecedents.AntecedentsResponse;
import liaison.linkit.profile.dto.response.education.EducationResponse;
import liaison.linkit.profile.dto.response.isValue.ProfileOnBoardingIsValueResponse;
import liaison.linkit.profile.dto.response.miniProfile.MiniProfileResponse;
import liaison.linkit.profile.dto.response.skill.ProfileSkillResponse;
import liaison.linkit.profile.dto.response.teamBuilding.ProfileTeamBuildingFieldResponse;
import liaison.linkit.profile.service.*;
import liaison.linkit.profile.service.onBoarding.OnBoardingService;
import liaison.linkit.region.dto.response.ProfileRegionResponse;
import liaison.linkit.region.service.ProfileRegionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;

@RestController
@RequiredArgsConstructor
@RequestMapping("/onBoarding")
@Slf4j
public class OnBoardingController {

    public final MemberService memberService;
    public final ProfileService profileService;
    public final OnBoardingService onBoardingService;
    public final MiniProfileService miniProfileService;
    public final ProfileTeamBuildingFieldService profileTeamBuildingFieldService;
    public final ProfileRegionService profileRegionService;
    public final ProfileSkillService profileSkillService;
    public final EducationService educationService;
    public final AntecedentsService antecedentsService;

    @GetMapping("/private")
    @MemberOnly
    public ResponseEntity<?> getOnBoardingProfile(@Auth final Accessor accessor) {
        log.info("내 이력서의 온보딩 정보 항목 조회 요청 발생");
        try {
            onBoardingService.validateProfileByMember(accessor.getMemberId());

            final ProfileOnBoardingIsValueResponse profileOnBoardingIsValueResponse
                    = onBoardingService.getProfileOnBoardingIsValue(accessor.getMemberId());

            final MiniProfileResponse miniProfileResponse
                    = getMiniProfileResponse(accessor.getMemberId(), profileOnBoardingIsValueResponse.isMiniProfile());
            log.info("miniProfileResponse={}", miniProfileResponse);

            final MemberNameResponse memberNameResponse
                    = getMemberNameResponse(accessor.getMemberId());
            log.info("memberNameResponse={}", memberNameResponse);

            final ProfileTeamBuildingFieldResponse profileTeamBuildingFieldResponse
                    = getProfileTeamBuildingResponse(accessor.getMemberId(), profileOnBoardingIsValueResponse.isProfileTeamBuildingField());
            log.info("profileTeamBuildingFieldResponse={}", profileTeamBuildingFieldResponse);

            final ProfileSkillResponse profileSkillResponse
                    = getProfileSkillResponse(accessor.getMemberId(), profileOnBoardingIsValueResponse.isProfileSkill());
            log.info("profileSkillResponse={}", profileSkillResponse);

            final ProfileRegionResponse profileRegionResponse
                    = getProfileRegionResponse(accessor.getMemberId(), profileOnBoardingIsValueResponse.isProfileRegion());
            log.info("profileRegionResponse={}", profileRegionResponse);

            final List<EducationResponse> educationResponses
                    = getEducationResponses(accessor.getMemberId(), profileOnBoardingIsValueResponse.isEducation());
            log.info("educationResponses={}", educationResponses);

            final List<AntecedentsResponse> antecedentsResponses
                    = getAntecedentsResponses(accessor.getMemberId(), profileOnBoardingIsValueResponse.isAntecedents());
            log.info("antecedentsResponses={}", antecedentsResponses);

            final OnBoardingProfileResponse onBoardingProfileResponse = onBoardingService.getOnBoardingProfile(
                    profileTeamBuildingFieldResponse,
                    profileSkillResponse,
                    profileRegionResponse,
                    educationResponses,
                    antecedentsResponses,
                    miniProfileResponse,
                    memberNameResponse
            );

            return ResponseEntity.ok().body(onBoardingProfileResponse);
        } catch (Exception e) {
            log.error("온보딩 조회 과정에서 예외 발생: {}", e.getMessage());
            return ResponseEntity.status(INTERNAL_SERVER_ERROR).body("온보딩 정보를 불러오는 과정에서 문제가 발생했습니다.");
        }
    }

    @PostMapping("/private/job/skill")
    @MemberOnly
    public ResponseEntity<Void> createOnBoardingPersonalJobAndSkill(
            @Auth final Accessor accessor,
            @RequestBody @Valid final OnBoardingPersonalJobAndSkillCreateRequest createRequest
    ) {
        onBoardingService.savePersonalJobAndRole(accessor.getMemberId(), createRequest.getJobRoleNames());
        onBoardingService.savePersonalSkill(accessor.getMemberId(), createRequest.getSkillNames());
        onBoardingService.updateMemberProfileType(accessor.getMemberId());
        return ResponseEntity.status(HttpStatus.CREATED).build();
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

    // 1.5.2. 희망 팀빌딩 분야 조회
    private ProfileTeamBuildingFieldResponse getProfileTeamBuildingResponse(
            final Long memberId,
            final boolean isProfileTeamBuildingField
    ) {
        if (isProfileTeamBuildingField) {
            profileTeamBuildingFieldService.validateProfileTeamBuildingFieldByMember(memberId);
            return profileTeamBuildingFieldService.getAllProfileTeamBuildings(memberId);
        } else {
            return new ProfileTeamBuildingFieldResponse();
        }
    }

    // 1.5.4. 보유 기술 조회
    private ProfileSkillResponse getProfileSkillResponse(
            final Long memberId,
            final boolean isProfileSkill
    ) {
        if (isProfileSkill) {
            profileSkillService.validateProfileSkillByMember(memberId);
            return profileSkillService.getAllProfileSkills(memberId);
        } else {
            return null;
        }
    }

    // 1.5.3. 활동 지역 조회
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

    // 1.5.6. 학력 조회
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

    // 1.5.7. 경력 조회
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
}
