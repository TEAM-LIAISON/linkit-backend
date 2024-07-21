package liaison.linkit.search.service;

import liaison.linkit.global.exception.BadRequestException;
import liaison.linkit.member.domain.Member;
import liaison.linkit.member.domain.MemberBasicInform;
import liaison.linkit.member.domain.repository.MemberBasicInformRepository;
import liaison.linkit.member.domain.repository.MemberRepository;
import liaison.linkit.profile.domain.Profile;
import liaison.linkit.profile.domain.miniProfile.MiniProfile;
import liaison.linkit.profile.domain.miniProfile.MiniProfileKeyword;
import liaison.linkit.profile.domain.repository.jobRole.ProfileJobRoleRepository;
import liaison.linkit.profile.domain.repository.miniProfile.MiniProfileKeywordRepository;
import liaison.linkit.profile.domain.repository.miniProfile.MiniProfileRepository;
import liaison.linkit.profile.domain.role.JobRole;
import liaison.linkit.profile.domain.role.ProfileJobRole;
import liaison.linkit.profile.dto.response.miniProfile.MiniProfileResponse;
import liaison.linkit.search.dto.response.SearchTeamProfileResponse;
import liaison.linkit.search.dto.response.browseAfterLogin.BrowseMiniProfileResponse;
import liaison.linkit.search.dto.response.browseAfterLogin.BrowseTeamMemberAnnouncementResponse;
import liaison.linkit.search.dto.response.browseAfterLogin.SearchBrowseTeamProfileResponse;
import liaison.linkit.team.domain.announcement.TeamMemberAnnouncement;
import liaison.linkit.team.domain.announcement.TeamMemberAnnouncementJobRole;
import liaison.linkit.team.domain.announcement.TeamMemberAnnouncementSkill;
import liaison.linkit.team.domain.miniprofile.TeamMiniProfile;
import liaison.linkit.team.domain.miniprofile.TeamMiniProfileKeyword;
import liaison.linkit.team.domain.repository.announcement.TeamMemberAnnouncementJobRoleRepository;
import liaison.linkit.team.domain.repository.announcement.TeamMemberAnnouncementRepository;
import liaison.linkit.team.domain.repository.announcement.TeamMemberAnnouncementSkillRepository;
import liaison.linkit.team.domain.repository.miniprofile.TeamMiniProfileKeywordRepository;
import liaison.linkit.team.domain.repository.miniprofile.TeamMiniProfileRepository;
import liaison.linkit.team.dto.response.announcement.TeamMemberAnnouncementResponse;
import liaison.linkit.team.dto.response.miniProfile.TeamMiniProfileResponse;
import liaison.linkit.wish.domain.repository.PrivateWishRepository;
import liaison.linkit.wish.domain.repository.TeamWishRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

import static liaison.linkit.global.exception.ExceptionCode.*;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class SearchService {

    private final MemberRepository memberRepository;
    private final MiniProfileRepository miniProfileRepository;
    private final MemberBasicInformRepository memberBasicInformRepository;
    private final MiniProfileKeywordRepository miniProfileKeywordRepository;
    private final ProfileJobRoleRepository profileJobRoleRepository;

    private final TeamMiniProfileRepository teamMiniProfileRepository;
    private final TeamMiniProfileKeywordRepository teamMiniProfileKeywordRepository;

    private final TeamMemberAnnouncementRepository teamMemberAnnouncementRepository;
    private final TeamMemberAnnouncementJobRoleRepository teamMemberAnnouncementJobRoleRepository;
    private final TeamMemberAnnouncementSkillRepository teamMemberAnnouncementSkillRepository;

    private final PrivateWishRepository privateWishRepository;
    private final TeamWishRepository teamWishRepository;

    // 회원 정보를 가져오는 메서드
    private Member getMember(final Long memberId) {
        return memberRepository.findById(memberId)
                .orElseThrow(() -> new BadRequestException(NOT_FOUND_MEMBER_BY_MEMBER_ID));
    }

    // 개인 미니 프로필 (페이지) 조회
    @Transactional(readOnly = true)
    public Page<MiniProfileResponse> findPrivateMiniProfile(
            final Pageable pageable,
            final List<String> teamBuildingFieldName,
            final List<String> jobRoleName,
            final List<String> skillName,
            final String cityName,
            String divisionName
    ) {
        log.info("pageable={}", pageable);
        log.info("teamBuildingFieldName={}", teamBuildingFieldName);
        log.info("jobRoleName={}", jobRoleName);
        log.info("skillName={}", skillName);
        log.info("cityName={}", cityName);
        log.info("divisionName={}", divisionName);

        // 미니 프로필 이력서에서 페이지네이션으로 조회
        final Page<MiniProfile> miniProfiles = miniProfileRepository.findAll(
                teamBuildingFieldName,
                jobRoleName,
                skillName,
                cityName,
                divisionName,
                pageable
        );

        log.info("miniProfiles.getNumberOfElements={}", miniProfiles.getNumberOfElements());
        return miniProfiles.map(this::convertToMiniProfileResponse);
    }

    @Transactional(readOnly = true)
    public Page<BrowseMiniProfileResponse> findPrivateMiniProfileLogin(
            final Long memberId,
            final Pageable pageable,
            final List<String> teamBuildingFieldName,
            final List<String> jobRoleName,
            final List<String> skillName,
            final String cityName,
            String divisionName
    ) {
        // 미니 프로필 이력서에서 페이지네이션으로 조회
        final Page<MiniProfile> miniProfiles = miniProfileRepository.findAll(
                teamBuildingFieldName,
                jobRoleName,
                skillName,
                cityName,
                divisionName,
                pageable
        );

        return miniProfiles.map(miniProfile -> convertToBrowseMiniProfileResponse(miniProfile, memberId));
    }


    // 팀원 공고, 팀 미니 프로필 응답 (페이지) 조회
    @Transactional(readOnly = true)
    public Page<SearchTeamProfileResponse> findTeamMemberAnnouncementsWithTeamMiniProfile(
            final Pageable pageable,
            // 팀 소개서에 해당
            final List<String> teamBuildingFieldName,
            // 팀원 공고에 해당
            final List<String> jobRoleName,
            // 팀원 공고에 해당
            final List<String> skillName,
            // 팀 소개서에 해당
            final String cityName,
            // 팀 소개서에 해당
            String divisionName,
            // 팀 소개서에 해당
            final List<String> activityTagName
    ) {
        // 해당 팀원 공고들을 찾는다.
        // 해당 팀원 공고와 연결된 팀 미니 프로필을 같이 반환한다.

        // 해당되는 모든 팀원 공고를 조회한다.
        Page<TeamMemberAnnouncement> teamMemberAnnouncements = teamMemberAnnouncementRepository.findAllByOrderByCreatedDateDesc(
                teamBuildingFieldName,
                jobRoleName,
                skillName,
                cityName,
                divisionName,
                activityTagName,
                pageable
        );
        return teamMemberAnnouncements.map(this::convertToSearchTeamProfileResponse);
    }

    // 팀원 공고, 팀 미니 프로필 응답 (페이지) 조회
    @Transactional(readOnly = true)
    public Page<SearchBrowseTeamProfileResponse> findTeamMemberAnnouncementsWithTeamMiniProfileAfterLogin(
            final Long memberId,
            final Pageable pageable,
            final List<String> teamBuildingFieldName,
            final List<String> jobRoleName,
            final List<String> skillName,
            final String cityName,
            String divisionName,
            final List<String> activityTagName
    ) {
        Page<TeamMemberAnnouncement> teamMemberAnnouncements = teamMemberAnnouncementRepository.findAllByOrderByCreatedDateDesc(
                teamBuildingFieldName,
                jobRoleName,
                skillName,
                cityName,
                divisionName,
                activityTagName,
                pageable
        );
        return teamMemberAnnouncements.map(teamMemberAnnouncement -> convertToSearchTeamProfileResponseAfterLogin(teamMemberAnnouncement, memberId));
    }


    private SearchTeamProfileResponse convertToSearchTeamProfileResponse(final TeamMemberAnnouncement teamMemberAnnouncement) {
        // 각각의 개별 팀원 공고를 찾아냈다.
        final TeamMiniProfile teamMiniProfile = getTeamMiniProfileByTeamProfileId(teamMemberAnnouncement.getTeamProfile().getId());
        final List<TeamMiniProfileKeyword> teamMiniProfileKeyword = teamMiniProfileKeywordRepository.findAllByTeamMiniProfileId(teamMiniProfile.getId());

        final TeamMemberAnnouncementJobRole teamMemberAnnouncementJobRole = getTeamMemberAnnouncementJobRole(teamMemberAnnouncement.getId());
        final List<TeamMemberAnnouncementSkill> teamMemberAnnouncementSkillList = getTeamMemberAnnouncementSkills(teamMemberAnnouncement.getId());
        final String teamName = teamMemberAnnouncement.getTeamProfile().getTeamMiniProfile().getTeamName();


        return new SearchTeamProfileResponse(
                TeamMiniProfileResponse.personalTeamMiniProfile(teamMiniProfile, teamMiniProfileKeyword),
                TeamMemberAnnouncementResponse.of(teamMemberAnnouncement, teamName, teamMemberAnnouncementJobRole, teamMemberAnnouncementSkillList)
        );
    }

    private SearchBrowseTeamProfileResponse convertToSearchTeamProfileResponseAfterLogin(final TeamMemberAnnouncement teamMemberAnnouncement, final Long memberId) {
        final TeamMiniProfile teamMiniProfile = getTeamMiniProfileByTeamProfileId(teamMemberAnnouncement.getTeamProfile().getId());
        final List<TeamMiniProfileKeyword> teamMiniProfileKeyword = teamMiniProfileKeywordRepository.findAllByTeamMiniProfileId(teamMiniProfile.getId());

        final TeamMemberAnnouncementJobRole teamMemberAnnouncementJobRole = getTeamMemberAnnouncementJobRole(teamMemberAnnouncement.getId());
        final List<TeamMemberAnnouncementSkill> teamMemberAnnouncementSkillList = getTeamMemberAnnouncementSkills(teamMemberAnnouncement.getId());
        final String teamName = teamMemberAnnouncement.getTeamProfile().getTeamMiniProfile().getTeamName();
        final boolean isTeamSaved = teamWishRepository.findByTeamMemberAnnouncementIdAndMemberId(teamMemberAnnouncement.getId(), memberId);

        return new SearchBrowseTeamProfileResponse(
                TeamMiniProfileResponse.personalTeamMiniProfile(teamMiniProfile, teamMiniProfileKeyword),
                BrowseTeamMemberAnnouncementResponse.of(teamMemberAnnouncement, teamName, teamMemberAnnouncementJobRole, teamMemberAnnouncementSkillList, isTeamSaved)
        );
    }

    private TeamMiniProfile getTeamMiniProfileByTeamProfileId(final Long teamProfileId) {
        return teamMiniProfileRepository.findByTeamProfileId(teamProfileId)
                .orElseThrow(() -> new BadRequestException(NOT_FOUND_TEAM_MINI_PROFILE_BY_TEAM_PROFILE_ID));
    }

    @Transactional(readOnly = true)
    public Page<TeamMiniProfileResponse> findTeamMiniProfile(
            final Pageable pageable,
            final String teamBuildingFieldName,
            final String jobRoleName,
            final String skillName,
            final String cityName,
            String divisionName,
            final String activityTagName
    ) {
        Page<TeamMiniProfile> teamMiniProfiles = teamMiniProfileRepository.findAllByOrderByCreatedDateDesc(
                teamBuildingFieldName,
                jobRoleName,
                skillName,
                cityName,
                divisionName,
                activityTagName,
                pageable
        );
        return teamMiniProfiles.map(this::convertToTeamMiniProfileResponse);
    }

    private TeamMiniProfileResponse convertToTeamMiniProfileResponse(final TeamMiniProfile teamMiniProfile) {
        List<String> teamKeywordNames = teamMiniProfileKeywordRepository.findAllByTeamMiniProfileId(teamMiniProfile.getId()).stream()
                .map(TeamMiniProfileKeyword::getTeamKeywordNames)
                .toList();

        return new TeamMiniProfileResponse(
                teamMiniProfile.getId(),
                teamMiniProfile.getIndustrySector().getSectorName(),
                teamMiniProfile.getTeamScale().getSizeType(),
                teamMiniProfile.getTeamName(),
                teamMiniProfile.getTeamProfileTitle(),
                teamMiniProfile.getIsTeamActivate(),
                teamMiniProfile.getTeamLogoImageUrl(),
                teamKeywordNames
        );
    }

    // 미니 프로필 객체를 응답 DTO 변환
    private MiniProfileResponse convertToMiniProfileResponse(final MiniProfile miniProfile) {
        final String memberName = getMemberNameByMiniProfile(miniProfile.getId());

        List<String> myKeywordNames = miniProfileKeywordRepository.findAllByMiniProfileId(miniProfile.getId()).stream()
                .map(MiniProfileKeyword::getMyKeywordNames)
                .collect(Collectors.toList());

        final List<ProfileJobRole> profileJobRoleList = getProfileJobRoleList(miniProfile.getProfile().getId());
        final List<JobRole> jobRoleList = profileJobRoleList.stream()
                .map(ProfileJobRole::getJobRole)
                .toList();

        final List<String> jobRoleNames = jobRoleList.stream()
                .map(JobRole::getJobRoleName)
                .toList();

        return new MiniProfileResponse(
                miniProfile.getId(),
                miniProfile.getProfileTitle(),
                miniProfile.getMiniProfileImg(),
                miniProfile.isActivate(),
                myKeywordNames,
                memberName,
                jobRoleNames
        );
    }

    private BrowseMiniProfileResponse convertToBrowseMiniProfileResponse(final MiniProfile miniProfile, final Long memberId) {
        final String memberName = getMemberNameByMiniProfile(miniProfile.getId());
        List<String> myKeywordNames = miniProfileKeywordRepository.findAllByMiniProfileId(miniProfile.getId()).stream()
                .map(MiniProfileKeyword::getMyKeywordNames)
                .toList();

        final List<ProfileJobRole> profileJobRoleList = getProfileJobRoleList(miniProfile.getProfile().getId());
        final List<JobRole> jobRoleList = profileJobRoleList.stream()
                .map(ProfileJobRole::getJobRole)
                .toList();

        final List<String> jobRoleNames = jobRoleList.stream()
                .map(JobRole::getJobRoleName)
                .toList();

        // privateWish -> 찾아야함 (내가 이 해당 미니 프로필을 찜해뒀는지?)
        final boolean isPrivateWish = privateWishRepository.findByMemberIdAndProfileId(memberId, miniProfile.getProfile().getId());

        return new BrowseMiniProfileResponse(
                miniProfile.getId(),
                miniProfile.getProfileTitle(),
                miniProfile.getMiniProfileImg(),
                miniProfile.isActivate(),
                myKeywordNames,
                memberName,
                jobRoleNames,
                isPrivateWish
        );
    }


    private List<ProfileJobRole> getProfileJobRoleList(final Long profileId) {
        return profileJobRoleRepository.findAllByProfileId(profileId);
    }

    @Transactional(readOnly = true)
    public String getMemberNameByMiniProfile(final Long miniProfileId) {
        final Profile profile = getProfileIdByMiniProfile(miniProfileId);
        // 오류 터짐

        final Member member = profile.getMember();
        final MemberBasicInform memberBasicInform = getMemberBasicInform(member.getId());
        return memberBasicInform.getMemberName();
    }

    private Profile getProfileIdByMiniProfile(final Long miniProfileId) {
        final MiniProfile miniProfile = miniProfileRepository.findById(miniProfileId)
                .orElseThrow(() -> new BadRequestException(NOT_FOUND_MINI_PROFILE_BY_ID));
        return miniProfile.getProfile();
    }

    // 회원 기본 정보를 가져오는 메서드
    private MemberBasicInform getMemberBasicInform(final Long memberId) {


        return memberBasicInformRepository.findByMemberId(memberId)
                .orElseThrow(() -> new BadRequestException(NOT_FOUND_MEMBER_BASIC_INFORM_BY_MEMBER_ID));
    }


    private TeamMemberAnnouncementJobRole getTeamMemberAnnouncementJobRole(final Long teamMemberAnnouncementId) {
        return teamMemberAnnouncementJobRoleRepository.findByTeamMemberAnnouncementId(teamMemberAnnouncementId)
                .orElseThrow(() -> new BadRequestException(NOT_FOUND_TEAM_MEMBER_ANNOUNCEMENT_JOB_ROLE));
    }

    private List<TeamMemberAnnouncementSkill> getTeamMemberAnnouncementSkills(final Long teamMemberAnnouncementId) {
        return teamMemberAnnouncementSkillRepository.findAllByTeamMemberAnnouncementId(teamMemberAnnouncementId);
    }



}
