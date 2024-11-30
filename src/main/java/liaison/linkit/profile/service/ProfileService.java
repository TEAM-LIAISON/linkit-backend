package liaison.linkit.profile.service;

import java.util.List;
import java.util.Map;
import liaison.linkit.common.business.RegionMapper;
import liaison.linkit.common.implement.RegionQueryAdapter;
import liaison.linkit.common.presentation.RegionResponseDTO.RegionDetail;
import liaison.linkit.profile.business.ProfileActivityMapper;
import liaison.linkit.profile.business.ProfileAwardsMapper;
import liaison.linkit.profile.business.ProfileCurrentStateMapper;
import liaison.linkit.profile.business.ProfileEducationMapper;
import liaison.linkit.profile.business.ProfileLicenseMapper;
import liaison.linkit.profile.business.ProfileLinkMapper;
import liaison.linkit.profile.business.ProfileMapper;
import liaison.linkit.profile.business.ProfilePortfolioMapper;
import liaison.linkit.profile.business.ProfilePositionMapper;
import liaison.linkit.profile.business.ProfileSkillMapper;
import liaison.linkit.profile.domain.Profile;
import liaison.linkit.profile.domain.ProfileCurrentState;
import liaison.linkit.profile.domain.ProfileLink;
import liaison.linkit.profile.domain.ProfilePosition;
import liaison.linkit.profile.domain.ProfileRegion;
import liaison.linkit.profile.domain.ProfileSkill;
import liaison.linkit.profile.domain.activity.ProfileActivity;
import liaison.linkit.profile.domain.awards.ProfileAwards;
import liaison.linkit.profile.domain.education.ProfileEducation;
import liaison.linkit.profile.domain.license.ProfileLicense;
import liaison.linkit.profile.domain.portfolio.ProfilePortfolio;
import liaison.linkit.profile.implement.ProfileLinkQueryAdapter;
import liaison.linkit.profile.implement.ProfileQueryAdapter;
import liaison.linkit.profile.implement.activity.ProfileActivityQueryAdapter;
import liaison.linkit.profile.implement.awards.ProfileAwardsQueryAdapter;
import liaison.linkit.profile.implement.education.ProfileEducationQueryAdapter;
import liaison.linkit.profile.implement.license.ProfileLicenseQueryAdapter;
import liaison.linkit.profile.implement.portfolio.ProfilePortfolioQueryAdapter;
import liaison.linkit.profile.implement.portfolio.ProjectRoleContributionQueryAdapter;
import liaison.linkit.profile.implement.position.ProfilePositionQueryAdapter;
import liaison.linkit.profile.implement.skill.ProfileSkillQueryAdapter;
import liaison.linkit.profile.presentation.activity.dto.ProfileActivityResponseDTO.ProfileActivityItem;
import liaison.linkit.profile.presentation.awards.dto.ProfileAwardsResponseDTO.ProfileAwardsItem;
import liaison.linkit.profile.presentation.education.dto.ProfileEducationResponseDTO.ProfileEducationItem;
import liaison.linkit.profile.presentation.license.dto.ProfileLicenseResponseDTO.ProfileLicenseItem;
import liaison.linkit.profile.presentation.link.dto.ProfileLinkResponseDTO.ProfileLinkItem;
import liaison.linkit.profile.presentation.miniProfile.dto.MiniProfileResponseDTO.ProfileCurrentStateItem;
import liaison.linkit.profile.presentation.portfolio.dto.ProfilePortfolioResponseDTO.ProfilePortfolioItem;
import liaison.linkit.profile.presentation.profile.dto.ProfileResponseDTO;
import liaison.linkit.profile.presentation.profile.dto.ProfileResponseDTO.ProfileBooleanMenu;
import liaison.linkit.profile.presentation.profile.dto.ProfileResponseDTO.ProfileCompletionMenu;
import liaison.linkit.profile.presentation.profile.dto.ProfileResponseDTO.ProfileInformMenu;
import liaison.linkit.profile.presentation.profile.dto.ProfileResponseDTO.ProfileLeftMenu;
import liaison.linkit.profile.presentation.profile.dto.ProfileResponseDTO.ProfilePositionDetail;
import liaison.linkit.profile.presentation.skill.dto.ProfileSkillResponseDTO.ProfileSkillItem;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class ProfileService {

    private final ProfileQueryAdapter profileQueryAdapter;
    private final ProfileMapper profileMapper;

    private final RegionQueryAdapter regionQueryAdapter;
    private final RegionMapper regionMapper;

    private final ProfilePositionQueryAdapter profilePositionQueryAdapter;
    private final ProfileSkillQueryAdapter profileSkillQueryAdapter;
    private final ProfileActivityQueryAdapter profileActivityQueryAdapter;
    private final ProfilePortfolioQueryAdapter profilePortfolioQueryAdapter;
    private final ProjectRoleContributionQueryAdapter projectRoleContributionQueryAdapter;
    private final ProfileEducationQueryAdapter profileEducationQueryAdapter;
    private final ProfileAwardsQueryAdapter profileAwardsQueryAdapter;
    private final ProfileLicenseQueryAdapter profileLicenseQueryAdapter;
    private final ProfileLinkQueryAdapter profileLinkQueryAdapter;

    private final ProfileCurrentStateMapper profileCurrentStateMapper;

    private final ProfilePositionMapper profilePositionMapper;
    private final ProfileSkillMapper profileSkillMapper;
    private final ProfileActivityMapper profileActivityMapper;
    private final ProfilePortfolioMapper profilePortfolioMapper;
    private final ProfileEducationMapper profileEducationMapper;
    private final ProfileAwardsMapper profileAwardsMapper;
    private final ProfileLicenseMapper profileLicenseMapper;
    private final ProfileLinkMapper profileLinkMapper;

    public ProfileLeftMenu getProfileLeftMenu(final Long memberId) {
        log.info("memberId = {}의 프로필 왼쪽 메뉴 DTO 조회 요청 발생했습니다.", memberId);

        final Profile profile = profileQueryAdapter.findByMemberId(memberId);
        log.info("profile = {}가 성공적으로 조회되었습니다.", profile);

        RegionDetail regionDetail = new RegionDetail();

        if (regionQueryAdapter.existsProfileRegionByProfileId((profile.getId()))) {
            final ProfileRegion profileRegion = regionQueryAdapter.findProfileRegionByProfileId(profile.getId());
            regionDetail = regionMapper.toRegionDetail(profileRegion.getRegion());
        }
        log.info("지역 정보 조회 성공");
        final List<ProfileCurrentState> profileCurrentStates = profileQueryAdapter.findProfileCurrentStatesByProfileId(profile.getId());
        final List<ProfileCurrentStateItem> profileCurrentStateItems = profileCurrentStateMapper.toProfileCurrentStateItems(profileCurrentStates);
        log.info("상태 정보 조회 성공");

        ProfilePositionDetail profilePositionDetail = new ProfilePositionDetail();

        if (profilePositionQueryAdapter.existsProfilePositionByProfileId(profile.getId())) {
            final ProfilePosition profilePosition = profilePositionQueryAdapter.findProfilePositionByProfileId(profile.getId());
            profilePositionDetail = profilePositionMapper.toProfilePositionDetail(profilePosition);
        }

        log.info("대분류 포지션 정보 조회 성공");

        final ProfileCompletionMenu profileCompletionMenu = profileMapper.toProfileCompletionMenu(profile);
        log.info("profileCompletionMenu = {}", profileCompletionMenu);
        final ProfileInformMenu profileInformMenu = profileMapper.toProfileInformMenu(profileCurrentStateItems, profile, profilePositionDetail, regionDetail);
        log.info("profileInformMenu = {}", profileInformMenu);
        final ProfileBooleanMenu profileBooleanMenu = profileMapper.toProfileBooleanMenu(profile);
        log.info("profileBooleanMenu = {}", profileBooleanMenu);

        return profileMapper.toProfileLeftMenu(profileCompletionMenu, profileInformMenu, profileBooleanMenu);
    }

    public ProfileResponseDTO.ProfileDetail getProfileDetail(final Long memberId, final Long profileId) {
        log.info("memberId = {}의 프로필 ID = {}에 대한 상세 조회 요청이 발생했습니다.", memberId, profileId);
        final Profile profile = profileQueryAdapter.findByMemberId(memberId);

        log.info("보유스킬 DTO 조회");
        final List<ProfileSkill> profileSkills = profileSkillQueryAdapter.getProfileSkills(memberId);
        final List<ProfileSkillItem> profileSkillItems = profileSkillMapper.profileSkillsToProfileSkillItems(profileSkills);

        log.info("이력 DTO 조회");
        final List<ProfileActivity> profileActivities = profileActivityQueryAdapter.getProfileActivities(memberId);
        final List<ProfileActivityItem> profileActivityItems = profileActivityMapper.profileActivitiesToProfileActivityItems(profileActivities);

        log.info("포트폴리오 DTO 조회");
        final List<ProfilePortfolio> profilePortfolios = profilePortfolioQueryAdapter.getProfilePortfolios(profile.getId());
        final Map<Long, List<String>> projectRolesMap = projectRoleContributionQueryAdapter.getProjectRolesByProfileId(profile.getId());
        final List<ProfilePortfolioItem> profilePortfolioItems = profilePortfolioMapper.profilePortfoliosToProfileProfilePortfolioItems(profilePortfolios, projectRolesMap);

        log.info("학력 DTO 조회");
        final List<ProfileEducation> profileEducations = profileEducationQueryAdapter.getProfileEducations(profile.getId());
        final List<ProfileEducationItem> profileEducationItems = profileEducationMapper.profileEducationsToProfileProfileEducationItems(profileEducations);

        log.info("수상 DTO 조회");
        final List<ProfileAwards> profileAwards = profileAwardsQueryAdapter.getProfileAwardsGroup(memberId);
        final List<ProfileAwardsItem> profileAwardsItems = profileAwardsMapper.profileEducationsToProfileProfileEducationItems(profileAwards);

        log.info("자격증 DTO 조회");
        final List<ProfileLicense> profileLicenses = profileLicenseQueryAdapter.getProfileLicenses(memberId);
        final List<ProfileLicenseItem> profileLicenseItems = profileLicenseMapper.profileLicensesToProfileLicenseItems(profileLicenses);

        log.info("링크 DTO 조회");
        final List<ProfileLink> profileLinks = profileLinkQueryAdapter.getProfileLinks(profile.getId());
        final List<ProfileLinkItem> profileLinkItems = profileLinkMapper.profileLinksToProfileLinkItems(profileLinks);

        return profileMapper.toProfileDetail(
                profileSkillItems,
                profileActivityItems,
                profilePortfolioItems,
                profileEducationItems,
                profileAwardsItems,
                profileLicenseItems,
                profileLinkItems
        );
    }
}
