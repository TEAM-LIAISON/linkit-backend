package liaison.linkit.search.service;

import liaison.linkit.member.domain.repository.member.MemberRepository;
import liaison.linkit.member.domain.repository.memberBasicInform.MemberBasicInformRepository;
import liaison.linkit.profile.domain.repository.jobRole.ProfileJobRoleRepository;
import liaison.linkit.scrap.domain.repository.privateScrap.PrivateScrapRepository;
import liaison.linkit.scrap.domain.repository.teamScrap.TeamScrapRepository;
import liaison.linkit.team.domain.repository.announcement.TeamMemberAnnouncementRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class SearchService {

    private final MemberRepository memberRepository;

    private final MemberBasicInformRepository memberBasicInformRepository;

    private final ProfileJobRoleRepository profileJobRoleRepository;

    private final TeamMemberAnnouncementRepository teamMemberAnnouncementRepository;

    private final PrivateScrapRepository privateScrapRepository;
    private final TeamScrapRepository teamScrapRepository;

//    // 개인 미니 프로필 (페이지) 조회
//    @Transactional(readOnly = true)
//    public Page<MiniProfileResponse> findPrivateMiniProfile(
//            final Pageable pageable,
//            final List<String> teamBuildingFieldName,
//            final List<String> jobRoleName,
//            final List<String> skillName,
//            final String cityName,
//            String divisionName
//    ) {
//        log.info("pageable={}", pageable);
//        log.info("teamBuildingFieldName={}", teamBuildingFieldName);
//        log.info("jobRoleName={}", jobRoleName);
//        log.info("skillName={}", skillName);
//        log.info("cityName={}", cityName);
//        log.info("divisionName={}", divisionName);
//
//        final Long teamBuildingFieldCount =
//                (teamBuildingFieldName != null) ? (long) teamBuildingFieldName.size() : null;
//        final Long jobRoleCount = (jobRoleName != null) ? (long) jobRoleName.size() : null;
//        final Long skillCount = (skillName != null) ? (long) skillName.size() : null;
//
//        // 미니 프로필 이력서에서 페이지네이션으로 조회
//        final Page<MiniProfile> miniProfiles = miniProfileRepository.findAll(
//                teamBuildingFieldName,
//                teamBuildingFieldCount,
//                jobRoleName,
//                jobRoleCount,
//                skillName,
//                skillCount,
//                cityName,
//                divisionName,
//                pageable
//        );
//
//        log.info("miniProfiles.getNumberOfElements={}", miniProfiles.getNumberOfElements());
//        return miniProfiles.map(this::convertToMiniProfileResponse);
//    }
//
//    @Transactional(readOnly = true)
//    public Page<BrowseMiniProfileResponse> findPrivateMiniProfileLogin(
//            final Long memberId,
//            final Pageable pageable,
//            final List<String> teamBuildingFieldName,
//            final List<String> jobRoleName,
//            final List<String> skillName,
//            final String cityName,
//            String divisionName
//    ) {
//
//        final Long teamBuildingFieldCount =
//                (teamBuildingFieldName != null) ? (long) teamBuildingFieldName.size() : null;
//        final Long jobRoleCount = (jobRoleName != null) ? (long) jobRoleName.size() : null;
//        final Long skillCount = (skillName != null) ? (long) skillName.size() : null;
//
//        // 미니 프로필 이력서에서 페이지네이션으로 조회
//        final Page<MiniProfile> miniProfiles = miniProfileRepository.findAll(
//                teamBuildingFieldName,
//                teamBuildingFieldCount,
//                jobRoleName,
//                jobRoleCount,
//                skillName,
//                skillCount,
//                cityName,
//                divisionName,
//                pageable
//        );
//
//        return miniProfiles.map(miniProfile -> convertToBrowseMiniProfileResponse(miniProfile, memberId));
//    }
//
//
//    // 팀원 공고, 팀 미니 프로필 응답 (페이지) 조회
//    @Transactional(readOnly = true)
//    public Page<SearchTeamProfileResponse> findTeamMemberAnnouncementsWithTeamMiniProfile(
//            final Pageable pageable,
//            // 팀 소개서에 해당
//            final List<String> teamBuildingFieldName,
//            // 팀원 공고에 해당
//            final List<String> jobRoleName,
//            // 팀원 공고에 해당
//            final List<String> skillName,
//            // 팀 소개서에 해당
//            final String cityName,
//            // 팀 소개서에 해당
//            String divisionName,
//            // 팀 소개서에 해당
//            final List<String> activityTagName
//    ) {
//        // 해당 팀원 공고들을 찾는다.
//        // 해당 팀원 공고와 연결된 팀 미니 프로필을 같이 반환한다.
//
//        final Long teamBuildingFieldCount =
//                (teamBuildingFieldName != null) ? (long) teamBuildingFieldName.size() : null;
//        final Long jobRoleCount = (jobRoleName != null) ? (long) jobRoleName.size() : null;
//        final Long skillCount = (skillName != null) ? (long) skillName.size() : null;
//        final Long activityTagCount = (activityTagName != null) ? (long) activityTagName.size() : null;
//
//        // 해당되는 모든 팀원 공고를 조회한다.
//        Page<TeamMemberAnnouncement> teamMemberAnnouncements = teamMemberAnnouncementRepository.findAllByOrderByCreatedDateDesc(
//                teamBuildingFieldName,
//                teamBuildingFieldCount,
//                jobRoleName,
//                jobRoleCount,
//                skillName,
//                skillCount,
//                cityName,
//                divisionName,
//                activityTagName,
//                activityTagCount,
//                pageable
//        );
//
//        return teamMemberAnnouncements.map(this::convertToSearchTeamProfileResponse);
//    }
//
//    // 팀원 공고, 팀 미니 프로필 응답 (페이지) 조회
//    @Transactional(readOnly = true)
//    public Page<SearchBrowseTeamProfileResponse> findTeamMemberAnnouncementsWithTeamMiniProfileAfterLogin(
//            final Long memberId,
//            final Pageable pageable,
//            final List<String> teamBuildingFieldName,
//            final List<String> jobRoleName,
//            final List<String> skillName,
//            final String cityName,
//            String divisionName,
//            final List<String> activityTagName
//    ) {
//        final Long teamBuildingFieldCount =
//                (teamBuildingFieldName != null) ? (long) teamBuildingFieldName.size() : null;
//        final Long jobRoleCount = (jobRoleName != null) ? (long) jobRoleName.size() : null;
//        final Long skillCount = (skillName != null) ? (long) skillName.size() : null;
//        final Long activityTagCount = (activityTagName != null) ? (long) activityTagName.size() : null;
//
//        // 해당되는 모든 팀원 공고를 조회한다.
//        Page<TeamMemberAnnouncement> teamMemberAnnouncements = teamMemberAnnouncementRepository.findAllByOrderByCreatedDateDesc(
//                teamBuildingFieldName,
//                teamBuildingFieldCount,
//                jobRoleName,
//                jobRoleCount,
//                skillName,
//                skillCount,
//                cityName,
//                divisionName,
//                activityTagName,
//                activityTagCount,
//                pageable
//        );
//        return teamMemberAnnouncements.map(
//                teamMemberAnnouncement -> convertToSearchTeamProfileResponseAfterLogin(teamMemberAnnouncement,
//                        memberId));
//    }
//
//
//    private SearchTeamProfileResponse convertToSearchTeamProfileResponse(
//            final TeamMemberAnnouncement teamMemberAnnouncement) {
//        // 각각의 개별 팀원 공고를 찾아냈다.
//        final TeamMiniProfile teamMiniProfile = getTeamMiniProfileByTeamProfileId(
//                teamMemberAnnouncement.getTeamProfile().getId());
//        final List<TeamMiniProfileKeyword> teamMiniProfileKeyword = teamMiniProfileKeywordRepository.findAllByTeamMiniProfileId(
//                teamMiniProfile.getId());
//
//        final TeamMemberAnnouncementJobRole teamMemberAnnouncementJobRole = getTeamMemberAnnouncementJobRole(
//                teamMemberAnnouncement.getId());
//        final List<TeamMemberAnnouncementSkill> teamMemberAnnouncementSkillList = getTeamMemberAnnouncementSkills(
//                teamMemberAnnouncement.getId());
//        final String teamName = teamMemberAnnouncement.getTeamProfile().getTeamMiniProfile().getTeamName();
//
//        return new SearchTeamProfileResponse(
//                TeamMiniProfileResponse.personalTeamMiniProfile(teamMiniProfile, teamMiniProfileKeyword),
//                TeamMemberAnnouncementResponse.of(
//                        teamMiniProfile.getTeamLogoImageUrl(), teamMemberAnnouncement, teamName,
//                        teamMemberAnnouncementJobRole, teamMemberAnnouncementSkillList)
//        );
//    }
//
//    private SearchBrowseTeamProfileResponse convertToSearchTeamProfileResponseAfterLogin(
//            final TeamMemberAnnouncement teamMemberAnnouncement, final Long memberId) {
//        final TeamMiniProfile teamMiniProfile = getTeamMiniProfileByTeamProfileId(
//                teamMemberAnnouncement.getTeamProfile().getId());
//        final List<TeamMiniProfileKeyword> teamMiniProfileKeyword = teamMiniProfileKeywordRepository.findAllByTeamMiniProfileId(
//                teamMiniProfile.getId());
//
//        final TeamMemberAnnouncementJobRole teamMemberAnnouncementJobRole = getTeamMemberAnnouncementJobRole(
//                teamMemberAnnouncement.getId());
//        final List<TeamMemberAnnouncementSkill> teamMemberAnnouncementSkillList = getTeamMemberAnnouncementSkills(
//                teamMemberAnnouncement.getId());
//        final String teamName = teamMemberAnnouncement.getTeamProfile().getTeamMiniProfile().getTeamName();
//        final boolean isTeamSaved = teamScrapRepository.existsByTeamMemberAnnouncementIdAndMemberId(
//                teamMemberAnnouncement.getId(), memberId);
//        log.info("isTeamSaved={}", isTeamSaved);
//
//        return new SearchBrowseTeamProfileResponse(
//                TeamMiniProfileResponse.personalTeamMiniProfile(teamMiniProfile, teamMiniProfileKeyword),
//                TeamMemberAnnouncementResponse.afterLogin(teamMiniProfile.getTeamLogoImageUrl(), teamMemberAnnouncement,
//                        teamName, teamMemberAnnouncementJobRole, teamMemberAnnouncementSkillList, isTeamSaved)
//        );
//    }
//
//    private TeamMiniProfile getTeamMiniProfileByTeamProfileId(final Long teamProfileId) {
//        return teamMiniProfileRepository.findByTeamProfileId(teamProfileId)
//                .orElseThrow(() -> new BadRequestException(NOT_FOUND_TEAM_MINI_PROFILE_BY_TEAM_PROFILE_ID));
//    }
//
//    @Transactional(readOnly = true)
//    public Page<TeamMiniProfileResponse> findTeamMiniProfile(
//            final Pageable pageable,
//            final String teamBuildingFieldName,
//            final String jobRoleName,
//            final String skillName,
//            final String cityName,
//            String divisionName,
//            final String activityTagName
//    ) {
//        Page<TeamMiniProfile> teamMiniProfiles = teamMiniProfileRepository.findAllByOrderByCreatedDateDesc(
//                teamBuildingFieldName,
//                jobRoleName,
//                skillName,
//                cityName,
//                divisionName,
//                activityTagName,
//                pageable
//        );
//        return teamMiniProfiles.map(this::convertToTeamMiniProfileResponse);
//    }
//
//    private TeamMiniProfileResponse convertToTeamMiniProfileResponse(final TeamMiniProfile teamMiniProfile) {
//        List<String> teamKeywordNames = teamMiniProfileKeywordRepository.findAllByTeamMiniProfileId(
//                        teamMiniProfile.getId()).stream()
//                .map(TeamMiniProfileKeyword::getTeamKeywordNames)
//                .toList();
//
//        return new TeamMiniProfileResponse(
//                teamMiniProfile.getId(),
//                teamMiniProfile.getIndustrySector().getSectorName(),
//                teamMiniProfile.getTeamScale().getSizeType(),
//                teamMiniProfile.getTeamName(),
//                teamMiniProfile.getTeamProfileTitle(),
//                teamMiniProfile.getIsTeamActivate(),
//                teamMiniProfile.getTeamLogoImageUrl(),
//                teamKeywordNames
//        );
//    }
//
//    // 미니 프로필 객체를 응답 DTO 변환
//    private MiniProfileResponse convertToMiniProfileResponse(final MiniProfile miniProfile) {
//        final String memberName = getMemberNameByMiniProfile(miniProfile.getId());
//
//        List<String> myKeywordNames = miniProfileKeywordRepository.findAllByMiniProfileId(miniProfile.getId()).stream()
//                .map(MiniProfileKeyword::getMyKeywordNames)
//                .collect(Collectors.toList());
//
//        final List<ProfileJobRole> profileJobRoleList = getProfileJobRoleList(miniProfile.getProfile().getId());
//        final List<JobRole> jobRoleList = profileJobRoleList.stream()
//                .map(ProfileJobRole::getJobRole)
//                .toList();
//
//        final List<String> jobRoleNames = jobRoleList.stream()
//                .map(JobRole::getJobRoleName)
//                .toList();
//
//        return new MiniProfileResponse(
//                miniProfile.getId(),
//                miniProfile.getProfileTitle(),
//                miniProfile.getMiniProfileImg(),
//                miniProfile.isActivate(),
//                myKeywordNames,
//                memberName,
//                jobRoleNames,
//                false
//        );
//    }
//
//    private BrowseMiniProfileResponse convertToBrowseMiniProfileResponse(final MiniProfile miniProfile,
//                                                                         final Long memberId) {
//        final String memberName = getMemberNameByMiniProfile(miniProfile.getId());
//        List<String> myKeywordNames = miniProfileKeywordRepository.findAllByMiniProfileId(miniProfile.getId()).stream()
//                .map(MiniProfileKeyword::getMyKeywordNames)
//                .toList();
//
//        final List<ProfileJobRole> profileJobRoleList = getProfileJobRoleList(miniProfile.getProfile().getId());
//        final List<JobRole> jobRoleList = profileJobRoleList.stream()
//                .map(ProfileJobRole::getJobRole)
//                .toList();
//
//        final List<String> jobRoleNames = jobRoleList.stream()
//                .map(JobRole::getJobRoleName)
//                .toList();
//
//        // privateWish -> 찾아야함 (내가 이 해당 미니 프로필을 찜해뒀는지?)
//        final boolean isPrivateWish = privateScrapRepository.existsByMemberIdAndProfileId(memberId,
//                miniProfile.getProfile().getId());
//
//        return new BrowseMiniProfileResponse(
//                miniProfile.getId(),
//                miniProfile.getProfileTitle(),
//                miniProfile.getMiniProfileImg(),
//                miniProfile.isActivate(),
//                myKeywordNames,
//                memberName,
//                jobRoleNames,
//                isPrivateWish
//        );
//    }
//
//
//    private List<ProfileJobRole> getProfileJobRoleList(final Long profileId) {
//        return profileJobRoleRepository.findAllByProfileId(profileId);
//    }
//
//    @Transactional(readOnly = true)
//    public String getMemberNameByMiniProfile(final Long miniProfileId) {
//        final Profile profile = getProfileIdByMiniProfile(miniProfileId);
//        // 오류 터짐
//
//        final Member member = profile.getMember();
//        final MemberBasicInform memberBasicInform = getMemberBasicInform(member.getId());
//        return memberBasicInform.getMemberName();
//    }
//
//    private Profile getProfileIdByMiniProfile(final Long miniProfileId) {
//        final MiniProfile miniProfile = miniProfileRepository.findById(miniProfileId)
//                .orElseThrow(() -> new BadRequestException(NOT_FOUND_MINI_PROFILE_BY_ID));
//        return miniProfile.getProfile();
//    }
//
//    // 회원 기본 정보를 가져오는 메서드
//    private MemberBasicInform getMemberBasicInform(final Long memberId) {
//
//        return memberBasicInformRepository.findByMemberId(memberId)
//                .orElseThrow(() -> new BadRequestException(NOT_FOUND_MEMBER_BASIC_INFORM_BY_MEMBER_ID));
//    }
//
//
//    private TeamMemberAnnouncementJobRole getTeamMemberAnnouncementJobRole(final Long teamMemberAnnouncementId) {
//        return teamMemberAnnouncementJobRoleRepository.findByTeamMemberAnnouncementId(teamMemberAnnouncementId)
//                .orElseThrow(() -> new BadRequestException(NOT_FOUND_TEAM_MEMBER_ANNOUNCEMENT_JOB_ROLE));
//    }
//
//    private List<TeamMemberAnnouncementSkill> getTeamMemberAnnouncementSkills(final Long teamMemberAnnouncementId) {
//        return teamMemberAnnouncementSkillRepository.findAllByTeamMemberAnnouncementId(teamMemberAnnouncementId);
//    }
//
//    @Transactional(readOnly = true)
//    public Slice<MiniProfileResponse> searchPrivateMiniProfile(
////            final Long memberId,
//            final Long lastIndex,
//            final Pageable pageable,
//            final List<String> teamBuildingFieldName,
//            final List<String> jobRoleName,
//            final List<String> skillName,
//            final String cityName,
//            final String divisionName
//    ) {
//        // MiniProfile 조회
//        final Slice<MiniProfile> miniProfiles = miniProfileRepository.searchAll(
////                memberId,
//                lastIndex,
//                pageable,
//                teamBuildingFieldName,
//                jobRoleName,
//                skillName,
//                cityName,
//                divisionName
//        );
//
//        // MiniProfile을 BrowseMiniProfileResponse로 변환
//        return miniProfiles.map(this::convertToMiniProfileResponse);
//    }

}
