package liaison.linkit.profile.presentation;

import liaison.linkit.auth.Auth;
import liaison.linkit.auth.MemberOnly;
import liaison.linkit.auth.domain.Accessor;
import liaison.linkit.profile.browse.CheckBrowseToPrivateProfileAccess;
import liaison.linkit.profile.dto.response.ProfileIntroductionResponse;
import liaison.linkit.profile.dto.response.antecedents.AntecedentsResponse;
import liaison.linkit.profile.dto.response.attach.AttachResponse;
import liaison.linkit.profile.dto.response.awards.AwardsResponse;
import liaison.linkit.profile.dto.response.browse.BrowsePrivateProfileResponse;
import liaison.linkit.profile.dto.response.completion.CompletionResponse;
import liaison.linkit.profile.dto.response.education.EducationResponse;
import liaison.linkit.profile.dto.response.isValue.ProfileIsValueResponse;
import liaison.linkit.profile.dto.response.miniProfile.MiniProfileResponse;
import liaison.linkit.profile.dto.response.onBoarding.JobAndSkillResponse;
import liaison.linkit.profile.dto.response.profileRegion.ProfileRegionResponse;
import liaison.linkit.profile.dto.response.teamBuilding.ProfileTeamBuildingFieldResponse;
import liaison.linkit.profile.service.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;

@RestController
@RequiredArgsConstructor
@RequestMapping
@Slf4j
public class BrowsePrivateProfileController {

    public final ProfileOnBoardingService profileOnBoardingService;
    public final ProfileService profileService;
    public final MiniProfileService miniProfileService;
    public final CompletionService completionService;
    public final TeamBuildingFieldService teamBuildingFieldService;
    public final AntecedentsService antecedentsService;
    public final EducationService educationService;
    public final AwardsService awardsService;
    public final AttachService attachService;
    public final ProfileRegionService profileRegionService;
    public final BrowsePrivateProfileService browsePrivateProfileService;

    // 타겟 이력서 열람 컨트롤러
    @GetMapping("/browse/private/profile/{miniProfileId}")
    @MemberOnly
    @CheckBrowseToPrivateProfileAccess
    public ResponseEntity<?> getBrowsePrivateProfile(
            @Auth final Accessor accessor,
            @PathVariable final Long miniProfileId
    ) {
        log.info("miniProfileId={}에 대한 내 이력서 열람 요청이 발생했습니다.", miniProfileId);
        try {
            // 1. 열람하고자 하는 내 이력서의 유효성을 판단한다.
            log.info("타겟 이력서 열람 유효성 검사 로직");
            browsePrivateProfileService.validatePrivateProfileByMiniProfile(miniProfileId);

            // 2. 열람하고자 하는 회원의 ID를 가져온다.
            final Long browseTargetPrivateProfileId = browsePrivateProfileService.getTargetPrivateProfileIdByMiniProfileId(miniProfileId);
            log.info("browseTargetPrivateProfileId={}", browseTargetPrivateProfileId);

            // 열람할 타깃 profile id 제공
            final ProfileIsValueResponse profileIsValueResponse = browsePrivateProfileService.getProfileIsValue(browseTargetPrivateProfileId);
            log.info("profileIsValueResponse={}", profileIsValueResponse);

            final MiniProfileResponse miniProfileResponse = getBrowseMiniProfileResponse(accessor.getMemberId(), browseTargetPrivateProfileId, profileIsValueResponse.isMiniProfile());
            log.info("miniProfileResponse={}", miniProfileResponse);

            final CompletionResponse completionResponse = getCompletionResponse(browseTargetPrivateProfileId);
            log.info("completionResponse={}", completionResponse);

            final ProfileIntroductionResponse profileIntroductionResponse = getProfileIntroduction(browseTargetPrivateProfileId, profileIsValueResponse.isIntroduction());
            log.info("profileIntroductionResponse={}", profileIntroductionResponse);

            final JobAndSkillResponse jobAndSkillResponse = getJobAndSkillResponse(browseTargetPrivateProfileId, profileIsValueResponse.isJobAndSkill());
            log.info("jobAndSkillResponse={}", jobAndSkillResponse);

            final ProfileTeamBuildingFieldResponse profileTeamBuildingFieldResponse = getProfileTeamBuildingResponse(browseTargetPrivateProfileId, profileIsValueResponse.isProfileTeamBuildingField());
            log.info("profileTeamBuildingFieldResponse={}", profileTeamBuildingFieldResponse);

            final ProfileRegionResponse profileRegionResponse = getProfileRegionResponse(browseTargetPrivateProfileId, profileIsValueResponse.isProfileRegion());
            log.info("profileRegionResponse={}", profileRegionResponse);

            final List<AntecedentsResponse> antecedentsResponses = getAntecedentsResponses(browseTargetPrivateProfileId, profileIsValueResponse.isAntecedents());
            log.info("antecedentsResponses={}", antecedentsResponses);

            final List<EducationResponse> educationResponses = getEducationResponses(browseTargetPrivateProfileId, profileIsValueResponse.isEducation());
            log.info("educationResponses={}", educationResponses);

            final List<AwardsResponse> awardsResponses = getAwardsResponses(browseTargetPrivateProfileId, profileIsValueResponse.isAwards());
            log.info("awardsResponses={}", awardsResponses);

            final AttachResponse attachResponse = getAttachResponses(browseTargetPrivateProfileId, profileIsValueResponse.isAttachUrl());
            log.info("attachResponse={}", attachResponse);

            final BrowsePrivateProfileResponse browsePrivateProfileResponse = browsePrivateProfileService.getProfileResponse(
                    browseTargetPrivateProfileId,
                    miniProfileResponse,
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

            return ResponseEntity.ok().body(browsePrivateProfileResponse);
        } catch (Exception e) {
            return ResponseEntity.status(INTERNAL_SERVER_ERROR).body("내 이력서를 조회하는 과정에서 문제가 발생했습니다.");
        }
    }

    private AttachResponse getAttachResponses(
            final Long browseTargetPrivateProfileId,
            final boolean isAttachUrl
    ) {
        if (isAttachUrl) {
            attachService.validateAttachUrlByProfile(browseTargetPrivateProfileId);
            return attachService.getBrowseAttachList(browseTargetPrivateProfileId);
        } else {
            return new AttachResponse();
        }
    }

    private List<AwardsResponse> getAwardsResponses(
            final Long browseTargetPrivateProfileId,
            final boolean isAwards
    ) {
        if (isAwards) {
            awardsService.validateAwardsByProfile(browseTargetPrivateProfileId);
            return awardsService.getAllBrowseAwards(browseTargetPrivateProfileId);
        } else {
            return null;
        }
    }

    private List<EducationResponse> getEducationResponses(
            final Long browseTargetPrivateProfileId,
            final boolean isEducation
    ) {
        if (isEducation) {
            educationService.validateEducationByProfile(browseTargetPrivateProfileId);
            return educationService.getAllBrowseEducations(browseTargetPrivateProfileId);
        } else {
            return null;
        }
    }

    private List<AntecedentsResponse> getAntecedentsResponses(
            final Long browseTargetPrivateProfileId,
            final boolean isAntecedents
    ) {
        if (isAntecedents) {
            antecedentsService.validateAntecedentsByProfile(browseTargetPrivateProfileId);
            return antecedentsService.getAllBrowseAntecedents(browseTargetPrivateProfileId);
        } else {
            return null;
        }
    }

    private ProfileRegionResponse getProfileRegionResponse(
            final Long browseTargetPrivateProfileId,
            final boolean isProfileRegion
    ) {
        if (isProfileRegion) {
            profileRegionService.validateProfileRegionByProfile(browseTargetPrivateProfileId);
            return profileRegionService.getPersonalBrowseProfileRegion(browseTargetPrivateProfileId);
        } else {
            return null;
        }
    }

    private ProfileTeamBuildingFieldResponse getProfileTeamBuildingResponse(
            final Long browseTargetPrivateProfileId,
            final boolean isProfileTeamBuildingField
    ) {
        if (isProfileTeamBuildingField) {
            teamBuildingFieldService.validateBrowseProfileTeamBuildingFieldByProfile(browseTargetPrivateProfileId);
            return teamBuildingFieldService.getAllBrowseProfileTeamBuildingFields(browseTargetPrivateProfileId);
        } else {
            return new ProfileTeamBuildingFieldResponse();
        }
    }


    // 보유 역량 및 기술 응답
    private JobAndSkillResponse getJobAndSkillResponse(
            final Long browseTargetPrivateProfileId,
            final boolean isJobAndSkill
    ) {
        if (isJobAndSkill) {
            return profileOnBoardingService.getBrowseJobAndSkill(browseTargetPrivateProfileId);
        } else {
            return new JobAndSkillResponse();
        }
    }

    private ProfileIntroductionResponse getProfileIntroduction(
            final Long browseTargetPrivateProfileId,
            final boolean isIntroduction
    ) {
        if (isIntroduction) {
            return profileService.getBrowseProfileIntroduction(browseTargetPrivateProfileId);
        } else {
            return new ProfileIntroductionResponse();
        }
    }

    // 내 이력서 프로필 완성도
    private CompletionResponse getCompletionResponse(
            final Long browseTargetPrivateProfileId
    ) {
        return completionService.getBrowseCompletion(browseTargetPrivateProfileId);
    }

    // 회원 미니 프로필
    private MiniProfileResponse getBrowseMiniProfileResponse(
            final Long memberId,
            final Long browseTargetPrivateProfileId,
            final boolean isMiniProfile
    ) {
        // 미니 프로필이 존재하는 경우
        if (isMiniProfile) {
            miniProfileService.validateMiniProfileByTargetProfileId(browseTargetPrivateProfileId);
            log.info("targetPrivateProfileId={}가 유효합니다. in browsePrivateProfileController", browseTargetPrivateProfileId);
            return miniProfileService.getBrowsePersonalMiniProfile(memberId, browseTargetPrivateProfileId);
        } else {
            final String memberName = miniProfileService.getMemberName(browseTargetPrivateProfileId);
            final List<String> jobRoleNames = miniProfileService.getJobRoleNames(browseTargetPrivateProfileId);
            return new MiniProfileResponse(memberName, jobRoleNames);
        }
    }
}
