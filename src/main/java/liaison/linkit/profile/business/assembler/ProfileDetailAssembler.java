package liaison.linkit.profile.business.assembler;

import java.util.List;
import java.util.Map;

import liaison.linkit.profile.business.mapper.ProfileActivityMapper;
import liaison.linkit.profile.business.mapper.ProfileAwardsMapper;
import liaison.linkit.profile.business.mapper.ProfileEducationMapper;
import liaison.linkit.profile.business.mapper.ProfileLicenseMapper;
import liaison.linkit.profile.business.mapper.ProfileLinkMapper;
import liaison.linkit.profile.business.mapper.ProfileLogMapper;
import liaison.linkit.profile.business.mapper.ProfileMapper;
import liaison.linkit.profile.business.mapper.ProfilePortfolioMapper;
import liaison.linkit.profile.business.mapper.ProfileSkillMapper;
import liaison.linkit.profile.domain.activity.ProfileActivity;
import liaison.linkit.profile.domain.awards.ProfileAwards;
import liaison.linkit.profile.domain.education.ProfileEducation;
import liaison.linkit.profile.domain.license.ProfileLicense;
import liaison.linkit.profile.domain.link.ProfileLink;
import liaison.linkit.profile.domain.log.ProfileLog;
import liaison.linkit.profile.domain.portfolio.ProfilePortfolio;
import liaison.linkit.profile.domain.profile.Profile;
import liaison.linkit.profile.domain.skill.ProfileSkill;
import liaison.linkit.profile.implement.activity.ProfileActivityQueryAdapter;
import liaison.linkit.profile.implement.awards.ProfileAwardsQueryAdapter;
import liaison.linkit.profile.implement.education.ProfileEducationQueryAdapter;
import liaison.linkit.profile.implement.license.ProfileLicenseQueryAdapter;
import liaison.linkit.profile.implement.link.ProfileLinkQueryAdapter;
import liaison.linkit.profile.implement.log.ProfileLogQueryAdapter;
import liaison.linkit.profile.implement.portfolio.ProfilePortfolioQueryAdapter;
import liaison.linkit.profile.implement.portfolio.ProjectRoleContributionQueryAdapter;
import liaison.linkit.profile.implement.skill.ProfileSkillQueryAdapter;
import liaison.linkit.profile.presentation.activity.dto.ProfileActivityResponseDTO.ProfileActivityItem;
import liaison.linkit.profile.presentation.awards.dto.ProfileAwardsResponseDTO.ProfileAwardsItem;
import liaison.linkit.profile.presentation.education.dto.ProfileEducationResponseDTO.ProfileEducationItem;
import liaison.linkit.profile.presentation.license.dto.ProfileLicenseResponseDTO.ProfileLicenseItem;
import liaison.linkit.profile.presentation.link.dto.ProfileLinkResponseDTO.ProfileLinkItem;
import liaison.linkit.profile.presentation.log.dto.ProfileLogResponseDTO.ProfileLogItem;
import liaison.linkit.profile.presentation.portfolio.dto.ProfilePortfolioResponseDTO.ProfilePortfolioItem;
import liaison.linkit.profile.presentation.profile.dto.ProfileResponseDTO;
import liaison.linkit.profile.presentation.profile.dto.ProfileResponseDTO.ProfileCompletionMenu;
import liaison.linkit.profile.presentation.profile.dto.ProfileResponseDTO.ProfileInformMenu;
import liaison.linkit.profile.presentation.skill.dto.ProfileSkillResponseDTO.ProfileSkillItem;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class ProfileDetailAssembler {

    // Adapter
    private final ProfileLogQueryAdapter profileLogQueryAdapter;
    private final ProfileSkillQueryAdapter profileSkillQueryAdapter;
    private final ProfileActivityQueryAdapter profileActivityQueryAdapter;
    private final ProfilePortfolioQueryAdapter profilePortfolioQueryAdapter;
    private final ProjectRoleContributionQueryAdapter projectRoleContributionQueryAdapter;
    private final ProfileEducationQueryAdapter profileEducationQueryAdapter;
    private final ProfileLinkQueryAdapter profileLinkQueryAdapter;
    private final ProfileLicenseQueryAdapter profileLicenseQueryAdapter;
    private final ProfileAwardsQueryAdapter profileAwardsQueryAdapter;

    // Mapper
    private final ProfileLogMapper profileLogMapper;
    private final ProfileSkillMapper profileSkillMapper;
    private final ProfileActivityMapper profileActivityMapper;
    private final ProfilePortfolioMapper profilePortfolioMapper;
    private final ProfileEducationMapper profileEducationMapper;
    private final ProfileAwardsMapper profileAwardsMapper;
    private final ProfileLicenseMapper profileLicenseMapper;
    private final ProfileLinkMapper profileLinkMapper;
    private final ProfileMapper profileMapper;

    public ProfileResponseDTO.ProfileDetail assembleProfileDetail(
            final Profile targetProfile,
            final boolean isMyProfile,
            final ProfileCompletionMenu profileCompletionMenu,
            final ProfileInformMenu profileInformMenu) {
        return profileMapper.toProfileDetail(
                isMyProfile,
                profileCompletionMenu,
                profileInformMenu,
                resolveProfileLogItem(targetProfile, isMyProfile),
                resolveProfileSkillItems(targetProfile.getMember().getId()),
                resolveProfileActivityItems(targetProfile.getMember().getId()),
                resolveProfilePortfolioItems(targetProfile),
                resolveProfileEducationItems(targetProfile),
                resolveProfileAwardsItems(targetProfile.getMember().getId()),
                resolveProfileLicenseItems(targetProfile.getMember().getId()),
                resolveProfileLinkItems(targetProfile));
    }

    /* ─── 헬퍼 메서드들 ─────────────────────────────────────────────── */

    // 프로필 로그 정보를 조회하여 ProfileLogItem으로 매핑
    private ProfileLogItem resolveProfileLogItem(
            final Profile targetProfile, final boolean isMyProfile) {

        if (isMyProfile) {
            if (profileLogQueryAdapter.existsRepresentativeProfileLogByProfile(
                    targetProfile.getId())) {
                ProfileLog profileLog =
                        profileLogQueryAdapter.getRepresentativeProfileLog(targetProfile.getId());
                return profileLogMapper.toProfileLogItem(profileLog);
            }
        } else {
            if (profileLogQueryAdapter.existsRepresentativePublicProfileLogByProfile(
                    targetProfile.getId())) {
                ProfileLog profileLog =
                        profileLogQueryAdapter.getRepresentativePublicProfileLog(
                                targetProfile.getId());
                return profileLogMapper.toProfileLogItem(profileLog);
            }
        }

        return new ProfileLogItem();
    }

    // 보유 스킬 정보를 조회하여 ProfileSkillItem 리스트로 변환
    private List<ProfileSkillItem> resolveProfileSkillItems(Long memberId) {
        List<ProfileSkill> profileSkills = profileSkillQueryAdapter.getProfileSkills(memberId);
        return profileSkillMapper.profileSkillsToProfileSkillItems(profileSkills);
    }

    // 이력 정보를 조회하여 ProfileActivityItem 리스트로 변환
    private List<ProfileActivityItem> resolveProfileActivityItems(Long memberId) {
        List<ProfileActivity> profileActivities =
                profileActivityQueryAdapter.getProfileActivities(memberId);
        return profileActivityMapper.profileActivitiesToProfileActivityItems(profileActivities);
    }

    // 포트폴리오 정보를 조회하여 ProfilePortfolioItem 리스트로 변환
    private List<ProfilePortfolioItem> resolveProfilePortfolioItems(Profile targetProfile) {
        List<ProfilePortfolio> profilePortfolios =
                profilePortfolioQueryAdapter.getProfilePortfolios(targetProfile.getId());
        Map<Long, List<String>> projectRolesMap =
                projectRoleContributionQueryAdapter.getProjectRolesByProfileId(
                        targetProfile.getId());
        return profilePortfolioMapper.profilePortfoliosToProfileProfilePortfolioItems(
                profilePortfolios, projectRolesMap);
    }

    // 학력 정보를 조회하여 ProfileEducationItem 리스트로 변환
    private List<ProfileEducationItem> resolveProfileEducationItems(Profile targetProfile) {
        List<ProfileEducation> profileEducations =
                profileEducationQueryAdapter.getProfileEducations(targetProfile.getId());
        return profileEducationMapper.profileEducationsToProfileProfileEducationItems(
                profileEducations);
    }

    // 수상 정보를 조회하여 ProfileAwardsItem 리스트로 변환
    private List<ProfileAwardsItem> resolveProfileAwardsItems(Long memberId) {
        List<ProfileAwards> profileAwards =
                profileAwardsQueryAdapter.getProfileAwardsGroup(memberId);
        return profileAwardsMapper.profileEducationsToProfileProfileEducationItems(profileAwards);
    }

    // 자격증 정보를 조회하여 ProfileLicenseItem 리스트로 변환
    private List<ProfileLicenseItem> resolveProfileLicenseItems(Long memberId) {
        List<ProfileLicense> profileLicenses =
                profileLicenseQueryAdapter.getProfileLicenses(memberId);
        return profileLicenseMapper.profileLicensesToProfileLicenseItems(profileLicenses);
    }

    // 링크 정보를 조회하여 ProfileLinkItem 리스트로 변환
    private List<ProfileLinkItem> resolveProfileLinkItems(Profile targetProfile) {
        List<ProfileLink> profileLinks =
                profileLinkQueryAdapter.getProfileLinks(targetProfile.getId());
        return profileLinkMapper.profileLinksToProfileLinkItems(profileLinks);
    }
}
