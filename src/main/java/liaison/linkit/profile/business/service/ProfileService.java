package liaison.linkit.profile.business.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import liaison.linkit.common.business.RegionMapper;
import liaison.linkit.common.implement.RegionQueryAdapter;
import liaison.linkit.common.presentation.RegionResponseDTO.RegionDetail;
import liaison.linkit.member.domain.Member;
import liaison.linkit.member.implement.MemberQueryAdapter;
import liaison.linkit.profile.business.mapper.ProfileActivityMapper;
import liaison.linkit.profile.business.mapper.ProfileAwardsMapper;
import liaison.linkit.profile.business.mapper.ProfileCurrentStateMapper;
import liaison.linkit.profile.business.mapper.ProfileEducationMapper;
import liaison.linkit.profile.business.mapper.ProfileLicenseMapper;
import liaison.linkit.profile.business.mapper.ProfileLinkMapper;
import liaison.linkit.profile.business.mapper.ProfileLogMapper;
import liaison.linkit.profile.business.mapper.ProfileMapper;
import liaison.linkit.profile.business.mapper.ProfilePortfolioMapper;
import liaison.linkit.profile.business.mapper.ProfilePositionMapper;
import liaison.linkit.profile.business.mapper.ProfileSkillMapper;
import liaison.linkit.profile.domain.activity.ProfileActivity;
import liaison.linkit.profile.domain.awards.ProfileAwards;
import liaison.linkit.profile.domain.education.ProfileEducation;
import liaison.linkit.profile.domain.license.ProfileLicense;
import liaison.linkit.profile.domain.link.ProfileLink;
import liaison.linkit.profile.domain.log.ProfileLog;
import liaison.linkit.profile.domain.portfolio.ProfilePortfolio;
import liaison.linkit.profile.domain.position.ProfilePosition;
import liaison.linkit.profile.domain.profile.Profile;
import liaison.linkit.profile.domain.region.ProfileRegion;
import liaison.linkit.profile.domain.skill.ProfileSkill;
import liaison.linkit.profile.domain.state.ProfileCurrentState;
import liaison.linkit.profile.implement.activity.ProfileActivityQueryAdapter;
import liaison.linkit.profile.implement.awards.ProfileAwardsQueryAdapter;
import liaison.linkit.profile.implement.education.ProfileEducationQueryAdapter;
import liaison.linkit.profile.implement.license.ProfileLicenseQueryAdapter;
import liaison.linkit.profile.implement.link.ProfileLinkQueryAdapter;
import liaison.linkit.profile.implement.log.ProfileLogQueryAdapter;
import liaison.linkit.profile.implement.portfolio.ProfilePortfolioQueryAdapter;
import liaison.linkit.profile.implement.portfolio.ProjectRoleContributionQueryAdapter;
import liaison.linkit.profile.implement.position.ProfilePositionQueryAdapter;
import liaison.linkit.profile.implement.profile.ProfileQueryAdapter;
import liaison.linkit.profile.implement.skill.ProfileSkillQueryAdapter;
import liaison.linkit.profile.presentation.activity.dto.ProfileActivityResponseDTO.ProfileActivityItem;
import liaison.linkit.profile.presentation.awards.dto.ProfileAwardsResponseDTO.ProfileAwardsItem;
import liaison.linkit.profile.presentation.education.dto.ProfileEducationResponseDTO.ProfileEducationItem;
import liaison.linkit.profile.presentation.license.dto.ProfileLicenseResponseDTO.ProfileLicenseItem;
import liaison.linkit.profile.presentation.link.dto.ProfileLinkResponseDTO.ProfileLinkItem;
import liaison.linkit.profile.presentation.log.dto.ProfileLogResponseDTO.ProfileLogItem;
import liaison.linkit.profile.presentation.miniProfile.dto.MiniProfileResponseDTO.ProfileCurrentStateItem;
import liaison.linkit.profile.presentation.portfolio.dto.ProfilePortfolioResponseDTO.ProfilePortfolioItem;
import liaison.linkit.profile.presentation.profile.dto.ProfileResponseDTO;
import liaison.linkit.profile.presentation.profile.dto.ProfileResponseDTO.ProfileScrapMenu;
import liaison.linkit.profile.presentation.profile.dto.ProfileResponseDTO.ProfileTeamInform;
import liaison.linkit.profile.presentation.profile.dto.ProfileResponseDTO.ProfileBooleanMenu;
import liaison.linkit.profile.presentation.profile.dto.ProfileResponseDTO.ProfileCompletionMenu;
import liaison.linkit.profile.presentation.profile.dto.ProfileResponseDTO.ProfileInformMenu;
import liaison.linkit.profile.presentation.profile.dto.ProfileResponseDTO.ProfileLeftMenu;
import liaison.linkit.profile.presentation.profile.dto.ProfileResponseDTO.ProfilePositionDetail;
import liaison.linkit.profile.presentation.skill.dto.ProfileSkillResponseDTO.ProfileSkillItem;
import liaison.linkit.scrap.implement.profileScrap.ProfileScrapQueryAdapter;
import liaison.linkit.team.business.mapper.teamMember.TeamMemberMapper;
import liaison.linkit.team.domain.team.Team;
import liaison.linkit.team.implement.teamMember.TeamMemberQueryAdapter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class ProfileService {

    /*
        Adapter
     */
    private final MemberQueryAdapter memberQueryAdapter;
    private final RegionQueryAdapter regionQueryAdapter;
    private final ProjectRoleContributionQueryAdapter projectRoleContributionQueryAdapter;

    private final ProfileQueryAdapter profileQueryAdapter;
    private final ProfilePositionQueryAdapter profilePositionQueryAdapter;
    private final ProfileLogQueryAdapter profileLogQueryAdapter;
    private final ProfileSkillQueryAdapter profileSkillQueryAdapter;
    private final ProfileActivityQueryAdapter profileActivityQueryAdapter;
    private final ProfilePortfolioQueryAdapter profilePortfolioQueryAdapter;
    private final ProfileEducationQueryAdapter profileEducationQueryAdapter;
    private final ProfileAwardsQueryAdapter profileAwardsQueryAdapter;
    private final ProfileLicenseQueryAdapter profileLicenseQueryAdapter;
    private final ProfileLinkQueryAdapter profileLinkQueryAdapter;
    private final ProfileScrapQueryAdapter profileScrapQueryAdapter;

    private final TeamMemberQueryAdapter teamMemberQueryAdapter;

    /*
        Mapper
     */
    private final RegionMapper regionMapper;

    private final ProfileMapper profileMapper;
    private final ProfileCurrentStateMapper profileCurrentStateMapper;
    private final ProfilePositionMapper profilePositionMapper;
    private final ProfileLogMapper profileLogMapper;
    private final ProfileSkillMapper profileSkillMapper;
    private final ProfileActivityMapper profileActivityMapper;
    private final ProfilePortfolioMapper profilePortfolioMapper;
    private final ProfileEducationMapper profileEducationMapper;
    private final ProfileAwardsMapper profileAwardsMapper;
    private final ProfileLicenseMapper profileLicenseMapper;
    private final ProfileLinkMapper profileLinkMapper;

    private final TeamMemberMapper teamMemberMapper;

    /*
        Method
     */

    // 수정창에서 내 프로필 왼쪽 메뉴 조회
    public ProfileLeftMenu getProfileLeftMenu(final Long memberId) {
        final Profile targetProfile = profileQueryAdapter.findByMemberId(memberId);

        final ProfileCompletionMenu targetProfileCompletionMenu = profileMapper.toProfileCompletionMenu(targetProfile);
        final ProfileBooleanMenu targetProfileBooleanMenu = profileMapper.toProfileBooleanMenu(targetProfile);

        return profileMapper.toProfileLeftMenu(targetProfileCompletionMenu, targetProfileBooleanMenu);
    }

    // 로그인한 사용자가 프로필을 조회한다.
    public ProfileResponseDTO.ProfileDetail getLoggedInProfileDetail(final Long memberId, final String emailId) {
        final Member member = memberQueryAdapter.findById(memberId);

        // 조회 요청을 보낸 회원이 조회하고자 한 프로필
        final Profile targetProfile = profileQueryAdapter.findByEmailId(emailId);
        final Member targetMember = targetProfile.getMember();

        boolean isMyProfile = targetMember.getId().equals(memberId);

        final boolean isProfileScrap = profileScrapQueryAdapter.existsByMemberIdAndEmailId(memberId,
            emailId);

        RegionDetail regionDetail = new RegionDetail();

        if (regionQueryAdapter.existsProfileRegionByProfileId((targetProfile.getId()))) {
            final ProfileRegion profileRegion = regionQueryAdapter.findProfileRegionByProfileId(
                targetProfile.getId());
            regionDetail = regionMapper.toRegionDetail(profileRegion.getRegion());
        }
        log.info("지역 정보 조회 성공");

        final List<ProfileCurrentState> profileCurrentStates = profileQueryAdapter.findProfileCurrentStatesByProfileId(
            targetProfile.getId());
        final List<ProfileCurrentStateItem> profileCurrentStateItems = profileCurrentStateMapper.toProfileCurrentStateItems(
            profileCurrentStates);
        log.info("상태 정보 조회 성공");

        ProfilePositionDetail profilePositionDetail = new ProfilePositionDetail();

        if (profilePositionQueryAdapter.existsProfilePositionByProfileId(targetProfile.getId())) {
            final ProfilePosition profilePosition = profilePositionQueryAdapter.findProfilePositionByProfileId(
                targetProfile.getId());
            profilePositionDetail = profilePositionMapper.toProfilePositionDetail(profilePosition);
        }

        List<ProfileTeamInform> targetProfileTeamInforms = new ArrayList<>();
        if (teamMemberQueryAdapter.existsTeamByMemberId(memberId) && isMyProfile) {
            final List<Team> myTeams = teamMemberQueryAdapter.getAllTeamsByMemberId(memberId);
            targetProfileTeamInforms = teamMemberMapper.toProfileTeamInforms(myTeams);
        } else if (teamMemberQueryAdapter.existsTeamByMemberId(memberId) && !isMyProfile) {
            final List<Team> targetProfilePublicTeams = teamMemberQueryAdapter.getAllPublicTeamsByMemberId(
                memberId);
            targetProfileTeamInforms = teamMemberMapper.toProfileTeamInforms(
                targetProfilePublicTeams);
        }

        final int profileScrapCount = profileScrapQueryAdapter.countTotalProfileScrapByEmailId(
            targetProfile.getMember().getEmailId());

        final ProfileCompletionMenu profileCompletionMenu = profileMapper.toProfileCompletionMenu(
            targetProfile);
        final ProfileInformMenu profileInformMenu = profileMapper.toProfileInformMenu(
            profileCurrentStateItems,
            isProfileScrap,
            profileScrapCount,
            targetProfile,
            profilePositionDetail,
            regionDetail,
            profileTeamInforms
        );

        log.info("대표 로그 DTO 조회");
        ProfileLogItem profileLogItem = new ProfileLogItem();
        if (profileLogQueryAdapter.existsProfileLogByProfileId(targetProfile.getId())) {
            final ProfileLog profileLog = profileLogQueryAdapter.getRepresentativeProfileLog(
                targetProfile.getId());
            profileLogItem = profileLogMapper.toProfileLogItem(profileLog);
        }

        log.info("보유스킬 DTO 조회");
        final List<ProfileSkill> profileSkills = profileSkillQueryAdapter.getProfileSkills(
            targetMember.getId());
        final List<ProfileSkillItem> profileSkillItems = profileSkillMapper.profileSkillsToProfileSkillItems(
            profileSkills);

        log.info("이력 DTO 조회");
        final List<ProfileActivity> profileActivities = profileActivityQueryAdapter.getProfileActivities(
            targetMember.getId());
        final List<ProfileActivityItem> profileActivityItems = profileActivityMapper.profileActivitiesToProfileActivityItems(
            profileActivities);

        log.info("포트폴리오 DTO 조회");
        final List<ProfilePortfolio> profilePortfolios = profilePortfolioQueryAdapter.getProfilePortfolios(
            targetProfile.getId());
        final Map<Long, List<String>> projectRolesMap = projectRoleContributionQueryAdapter.getProjectRolesByProfileId(
            targetProfile.getId());
        final List<ProfilePortfolioItem> profilePortfolioItems = profilePortfolioMapper.profilePortfoliosToProfileProfilePortfolioItems(
            profilePortfolios, projectRolesMap);

        log.info("학력 DTO 조회");
        final List<ProfileEducation> profileEducations = profileEducationQueryAdapter.getProfileEducations(
            targetProfile.getId());
        final List<ProfileEducationItem> profileEducationItems = profileEducationMapper.profileEducationsToProfileProfileEducationItems(
            profileEducations);

        log.info("수상 DTO 조회");
        final List<ProfileAwards> profileAwards = profileAwardsQueryAdapter.getProfileAwardsGroup(
            targetMember.getId());
        final List<ProfileAwardsItem> profileAwardsItems = profileAwardsMapper.profileEducationsToProfileProfileEducationItems(
            profileAwards);

        log.info("자격증 DTO 조회");
        final List<ProfileLicense> profileLicenses = profileLicenseQueryAdapter.getProfileLicenses(
            targetMember.getId());
        final List<ProfileLicenseItem> profileLicenseItems = profileLicenseMapper.profileLicensesToProfileLicenseItems(
            profileLicenses);

        log.info("링크 DTO 조회");
        final List<ProfileLink> profileLinks = profileLinkQueryAdapter.getProfileLinks(
            targetProfile.getId());
        final List<ProfileLinkItem> profileLinkItems = profileLinkMapper.profileLinksToProfileLinkItems(
            profileLinks);

        return profileMapper.toProfileDetail(
            isMyProfile,
            profileCompletionMenu,
            profileInformMenu,
            profileScrapCount,
            profileLogItem,
            profileSkillItems,
            profileActivityItems,
            profilePortfolioItems,
            profileEducationItems,
            profileAwardsItems,
            profileLicenseItems,
            profileLinkItems
        );
    }

    // 로그인하지 않은 사용자가 프로필을 조회한다.
    public ProfileResponseDTO.ProfileDetail getLoggedOutProfileDetail(final String emailId) {
        log.info("이메일 ID = {}에 대한 상세 조회 요청이 발생했습니다.", emailId);
        final Profile targetProfile = profileQueryAdapter.findByEmailId(emailId);
        final Member targetMember = targetProfile.getMember();

        RegionDetail regionDetail = new RegionDetail();

        if (regionQueryAdapter.existsProfileRegionByProfileId((targetProfile.getId()))) {
            final ProfileRegion profileRegion = regionQueryAdapter.findProfileRegionByProfileId(
                targetProfile.getId());
            regionDetail = regionMapper.toRegionDetail(profileRegion.getRegion());
        }

        log.info("지역 정보 조회 성공");
        final List<ProfileCurrentState> profileCurrentStates = profileQueryAdapter.findProfileCurrentStatesByProfileId(
            targetProfile.getId());
        final List<ProfileCurrentStateItem> profileCurrentStateItems = profileCurrentStateMapper.toProfileCurrentStateItems(
            profileCurrentStates);
        log.info("상태 정보 조회 성공");

        ProfilePositionDetail profilePositionDetail = new ProfilePositionDetail();

        if (profilePositionQueryAdapter.existsProfilePositionByProfileId(targetProfile.getId())) {
            final ProfilePosition profilePosition = profilePositionQueryAdapter.findProfilePositionByProfileId(
                targetProfile.getId());
            profilePositionDetail = profilePositionMapper.toProfilePositionDetail(profilePosition);
        }
        log.info("대분류 포지션 정보 조회 성공");

        List<ProfileTeamInform> profileTeamInforms = new ArrayList<>();
        if (teamMemberQueryAdapter.existsTeamByMemberId(targetMember.getId())) {
            final List<Team> myTeams = teamMemberQueryAdapter.getAllTeamsByMemberId(
                targetMember.getId());
            profileTeamInforms = teamMemberMapper.toProfileTeamInforms(myTeams);
            log.info("팀 정보 조회 성공, 팀 수: {}", profileTeamInforms.size());
        }

        final int profileScrapCount = profileScrapQueryAdapter.countTotalProfileScrapByEmailId(
            emailId);

        final ProfileCompletionMenu profileCompletionMenu = profileMapper.toProfileCompletionMenu(
            targetProfile);
        final ProfileInformMenu profileInformMenu = profileMapper.toProfileInformMenu(
            profileCurrentStateItems, false, profileScrapCount, targetProfile,
            profilePositionDetail, regionDetail,
            profileTeamInforms);

        log.info("대표 로그 DTO 조회");
        ProfileLogItem profileLogItem = new ProfileLogItem();
        if (profileLogQueryAdapter.existsProfileLogByProfileId(targetProfile.getId())) {
            final ProfileLog profileLog = profileLogQueryAdapter.getRepresentativeProfileLog(
                targetProfile.getId());
            profileLogItem = profileLogMapper.toProfileLogItem(profileLog);
        }

        log.info("보유스킬 DTO 조회");
        final List<ProfileSkill> profileSkills = profileSkillQueryAdapter.getProfileSkills(
            targetMember.getId());
        final List<ProfileSkillItem> profileSkillItems = profileSkillMapper.profileSkillsToProfileSkillItems(
            profileSkills);

        log.info("이력 DTO 조회");
        final List<ProfileActivity> profileActivities = profileActivityQueryAdapter.getProfileActivities(
            targetMember.getId());
        final List<ProfileActivityItem> profileActivityItems = profileActivityMapper.profileActivitiesToProfileActivityItems(
            profileActivities);

        log.info("포트폴리오 DTO 조회");
        final List<ProfilePortfolio> profilePortfolios = profilePortfolioQueryAdapter.getProfilePortfolios(
            targetProfile.getId());
        final Map<Long, List<String>> projectRolesMap = projectRoleContributionQueryAdapter.getProjectRolesByProfileId(
            targetMember.getId());
        final List<ProfilePortfolioItem> profilePortfolioItems = profilePortfolioMapper.profilePortfoliosToProfileProfilePortfolioItems(
            profilePortfolios, projectRolesMap);

        log.info("학력 DTO 조회");
        final List<ProfileEducation> profileEducations = profileEducationQueryAdapter.getProfileEducations(
            targetProfile.getId());
        log.info("학력 DTO 조회 에러 체크 1");
        final List<ProfileEducationItem> profileEducationItems = profileEducationMapper.profileEducationsToProfileProfileEducationItems(
            profileEducations);
        log.info("학력 DTO 조회 에러 체크 2");

        log.info("수상 DTO 조회");
        final List<ProfileAwards> profileAwards = profileAwardsQueryAdapter.getProfileAwardsGroup(
            targetMember.getId());
        final List<ProfileAwardsItem> profileAwardsItems = profileAwardsMapper.profileEducationsToProfileProfileEducationItems(
            profileAwards);

        log.info("자격증 DTO 조회");
        final List<ProfileLicense> profileLicenses = profileLicenseQueryAdapter.getProfileLicenses(
            targetMember.getId());
        final List<ProfileLicenseItem> profileLicenseItems = profileLicenseMapper.profileLicensesToProfileLicenseItems(
            profileLicenses);

        log.info("링크 DTO 조회");
        final List<ProfileLink> profileLinks = profileLinkQueryAdapter.getProfileLinks(
            targetProfile.getId());
        final List<ProfileLinkItem> profileLinkItems = profileLinkMapper.profileLinksToProfileLinkItems(
            profileLinks);

        return profileMapper.toProfileDetail(
            false,
            profileCompletionMenu,
            profileInformMenu,
            targetProfile.getMember().getProfileScrapCount(),
            profileLogItem,
            profileSkillItems,
            profileActivityItems,
            profilePortfolioItems,
            profileEducationItems,
            profileAwardsItems,
            profileLicenseItems,
            profileLinkItems
        );
    }

    // 홈화면에서 로그인 상태에서 팀원 정보를 조회한다.
    public ProfileResponseDTO.ProfileInformMenus getHomeProfileInformMenusInLoginState(
        final Long memberId) {
        // 최대 6개의 Profile 조회
        List<Profile> profiles = profileQueryAdapter.findTopProfiles(6);

        // Profiles -> ProfileInformMenus 변환
        List<ProfileResponseDTO.ProfileInformMenu> profileInformMenus = profiles.stream()
            .map(profile -> toHomeProfileInformMenuInLoginState(profile, memberId))
            .toList();

        // ProfileInformMenus DTO 반환
        return profileMapper.toProfileInformMenus(profileInformMenus);
    }

    // 홈화면에서 로그아웃 상태에서 팀원 정보를 조회한다.
    public ProfileResponseDTO.ProfileInformMenus getHomeProfileInformMenusInLogoutState() {
        // 최대 6개의 Profile 조회
        List<Profile> profiles = profileQueryAdapter.findTopProfiles(6);

        // Profiles -> ProfileInformMenus 변환
        List<ProfileResponseDTO.ProfileInformMenu> profileInformMenus = profiles.stream()
            .map(this::toHomeProfileInformMenuInLogoutState)
            .toList();

        // ProfileInformMenus DTO 반환
        return profileMapper.toProfileInformMenus(profileInformMenus);
    }


    // 개별 Profile -> ProfileInformMenu 변환
    private ProfileInformMenu toHomeProfileInformMenuInLoginState(final Profile profile,
        final Long memberId) {
        // 지역 정보
        RegionDetail regionDetail = fetchRegionDetail(profile);

        // 상태 정보
        List<ProfileCurrentStateItem> profileCurrentStateItems = fetchProfileCurrentStateItems(
            profile);

        // 포지션 정보
        ProfilePositionDetail profilePositionDetail = fetchProfilePositionDetail(profile);

        // 팀 정보
        List<ProfileTeamInform> profileTeamInforms = fetchProfileTeamInforms(profile);

        // 프로필 스크랩 여부
        final boolean isProfileScrap = profileScrapQueryAdapter.existsByMemberIdAndEmailId(memberId,
            profile.getMember().getEmailId());

        // 스크랩 수 계산
        int profileScrapCount = profileScrapQueryAdapter.countTotalProfileScrapByEmailId(
            profile.getMember().getEmailId());

        // ProfileInformMenu 생성 및 반환
        return profileMapper.toProfileInformMenu(
            profileCurrentStateItems, isProfileScrap, profileScrapCount, profile,
            profilePositionDetail, regionDetail, profileTeamInforms
        );
    }

    private ProfileInformMenu toHomeProfileInformMenuInLogoutState(final Profile profile) {
        // 지역 정보
        RegionDetail regionDetail = fetchRegionDetail(profile);

        // 상태 정보
        List<ProfileCurrentStateItem> profileCurrentStateItems = fetchProfileCurrentStateItems(
            profile);

        // 포지션 정보
        ProfilePositionDetail profilePositionDetail = fetchProfilePositionDetail(profile);

        // 팀 정보
        List<ProfileTeamInform> profileTeamInforms = fetchProfileTeamInforms(profile);

        // 스크랩 수 계산
        int profileScrapCount = profileScrapQueryAdapter.countTotalProfileScrapByEmailId(
            profile.getMember().getEmailId());

        // ProfileInformMenu 생성 및 반환
        return profileMapper.toProfileInformMenu(
            profileCurrentStateItems, false, profileScrapCount, profile,
            profilePositionDetail, regionDetail, profileTeamInforms
        );
    }

    // 지역 정보 가져오기
    private RegionDetail fetchRegionDetail(Profile profile) {
        if (regionQueryAdapter.existsProfileRegionByProfileId(profile.getId())) {
            ProfileRegion profileRegion = regionQueryAdapter.findProfileRegionByProfileId(
                profile.getId());
            log.info("지역 정보 조회 성공");
            return regionMapper.toRegionDetail(profileRegion.getRegion());
        }
        return new RegionDetail();
    }

    // 상태 정보 가져오기
    private List<ProfileCurrentStateItem> fetchProfileCurrentStateItems(Profile profile) {
        List<ProfileCurrentState> profileCurrentStates =
            profileQueryAdapter.findProfileCurrentStatesByProfileId(profile.getId());
        log.info("상태 정보 조회 성공");
        return profileCurrentStateMapper.toProfileCurrentStateItems(profileCurrentStates);
    }

    // 포지션 정보 가져오기
    private ProfilePositionDetail fetchProfilePositionDetail(Profile profile) {
        if (profilePositionQueryAdapter.existsProfilePositionByProfileId(profile.getId())) {
            ProfilePosition profilePosition = profilePositionQueryAdapter.findProfilePositionByProfileId(
                profile.getId());
            log.info("포지션 정보 조회 성공");
            return profilePositionMapper.toProfilePositionDetail(profilePosition);
        }
        return new ProfilePositionDetail();
    }

    // 팀 정보 가져오기
    private List<ProfileTeamInform> fetchProfileTeamInforms(Profile profile) {
        if (teamMemberQueryAdapter.existsTeamByMemberId(profile.getMember().getId())) {
            List<Team> myTeams = teamMemberQueryAdapter.getAllTeamsByMemberId(
                profile.getMember().getId());
            log.info("팀 정보 조회 성공, 팀 수: {}", myTeams.size());
            return teamMemberMapper.toProfileTeamInforms(myTeams);
        }
        return new ArrayList<>();
    }

}
