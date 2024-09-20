package liaison.linkit.wish.business;

import static liaison.linkit.global.exception.ExceptionCode.NOT_FOUND_TEAM_MEMBER_ANNOUNCEMENT_ID;
import static liaison.linkit.global.exception.ExceptionCode.NOT_FOUND_TEAM_MEMBER_ANNOUNCEMENT_JOB_ROLE;
import static liaison.linkit.global.exception.ExceptionCode.NOT_FOUND_TEAM_MINI_PROFILE_BY_TEAM_PROFILE_ID;
import static liaison.linkit.global.exception.ExceptionCode.NOT_FOUND_TEAM_PROFILE_ID;

import java.util.List;
import java.util.Objects;
import liaison.linkit.global.exception.BadRequestException;
import liaison.linkit.member.domain.Member;
import liaison.linkit.member.domain.MemberBasicInform;
import liaison.linkit.member.implement.MemberBasicInformQueryAdapter;
import liaison.linkit.member.implement.MemberQueryAdapter;
import liaison.linkit.profile.domain.Profile;
import liaison.linkit.profile.domain.miniProfile.MiniProfile;
import liaison.linkit.profile.domain.miniProfile.MiniProfileKeyword;
import liaison.linkit.profile.domain.repository.jobRole.ProfileJobRoleRepository;
import liaison.linkit.profile.domain.repository.miniProfile.MiniProfileKeywordRepository;
import liaison.linkit.profile.domain.role.JobRole;
import liaison.linkit.profile.domain.role.ProfileJobRole;
import liaison.linkit.profile.implement.ProfileQueryAdapter;
import liaison.linkit.search.dto.response.browseAfterLogin.BrowseMiniProfileResponse;
import liaison.linkit.team.domain.TeamProfile;
import liaison.linkit.team.domain.announcement.TeamMemberAnnouncement;
import liaison.linkit.team.domain.announcement.TeamMemberAnnouncementJobRole;
import liaison.linkit.team.domain.announcement.TeamMemberAnnouncementSkill;
import liaison.linkit.team.domain.miniprofile.TeamMiniProfile;
import liaison.linkit.team.domain.miniprofile.TeamMiniProfileKeyword;
import liaison.linkit.team.domain.repository.announcement.TeamMemberAnnouncementJobRoleRepository;
import liaison.linkit.team.domain.repository.announcement.TeamMemberAnnouncementRepository;
import liaison.linkit.team.domain.repository.announcement.TeamMemberAnnouncementSkillRepository;
import liaison.linkit.team.domain.repository.miniprofile.TeamMiniProfileRepository;
import liaison.linkit.team.domain.repository.miniprofile.teamMiniProfileKeyword.TeamMiniProfileKeywordRepository;
import liaison.linkit.team.domain.repository.teamProfile.TeamProfileRepository;
import liaison.linkit.team.dto.response.announcement.TeamMemberAnnouncementResponse;
import liaison.linkit.team.dto.response.miniProfile.TeamMiniProfileResponse;
import liaison.linkit.wish.domain.PrivateWish;
import liaison.linkit.wish.domain.TeamWish;
import liaison.linkit.wish.domain.repository.privateWish.PrivateWishRepository;
import liaison.linkit.wish.domain.repository.teamWish.TeamWishRepository;
import liaison.linkit.wish.exception.privateWish.ForbiddenPrivateWishException;
import liaison.linkit.wish.exception.privateWish.PrivateWishManyRequestException;
import liaison.linkit.wish.exception.teamWish.ForbiddenTeamWishException;
import liaison.linkit.wish.exception.teamWish.TeamWishManyRequestException;
import liaison.linkit.wish.implement.privateWish.PrivateWishCommandAdapter;
import liaison.linkit.wish.implement.privateWish.PrivateWishQueryAdapter;
import liaison.linkit.wish.implement.teamWish.TeamWishCommandAdapter;
import liaison.linkit.wish.implement.teamWish.TeamWishQueryAdapter;
import liaison.linkit.wish.presentation.dto.privateWish.PrivateWishResponseDTO;
import liaison.linkit.wish.presentation.dto.response.WishTeamProfileResponse;
import liaison.linkit.wish.presentation.dto.teamWish.TeamWishResponseDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class WishService {

    private final MemberQueryAdapter memberQueryAdapter;

    private final MemberBasicInformQueryAdapter memberBasicInformQueryAdapter;

    private final PrivateWishMapper privateWishMapper;
    private final PrivateWishQueryAdapter privateWishQueryAdapter;
    private final PrivateWishCommandAdapter privateWishCommandAdapter;

    private final TeamWishMapper teamWishMapper;
    private final TeamWishQueryAdapter teamWishQueryAdapter;
    private final TeamWishCommandAdapter teamWishCommandAdapter;

    private final ProfileQueryAdapter profileQueryAdapter;

    private final TeamMiniProfileRepository teamMiniProfileRepository;
    private final PrivateWishRepository privateWishRepository;
    private final TeamWishRepository teamWishRepository;
    private final ProfileJobRoleRepository profileJobRoleRepository;
    private final MiniProfileKeywordRepository miniProfileKeywordRepository;
    private final TeamMiniProfileKeywordRepository teamMiniProfileKeywordRepository;
    private final TeamProfileRepository teamProfileRepository;

    private final TeamMemberAnnouncementRepository teamMemberAnnouncementRepository;
    private final TeamMemberAnnouncementJobRoleRepository teamMemberAnnouncementJobRoleRepository;
    private final TeamMemberAnnouncementSkillRepository teamMemberAnnouncementSkillRepository;

    private TeamProfile getTeamProfile(final Long memberId) {
        return teamProfileRepository.findByMemberId(memberId)
                .orElseThrow(() -> new BadRequestException(NOT_FOUND_TEAM_PROFILE_ID));
    }

    @Transactional
    public PrivateWishResponseDTO.AddPrivateWish createWishToPrivateProfile(final Long memberId, final Long profileId) {
        final Member member = memberQueryAdapter.findById(memberId);
        final Profile profile = profileQueryAdapter.findById(profileId);
        if (Objects.equals(profileQueryAdapter.findByMemberId(memberId).getId(), profile.getId())) {
            throw ForbiddenPrivateWishException.EXCEPTION;
        }
        privateWishCommandAdapter.create(new PrivateWish(null, member, profile));
        member.addPrivateWishCount();
        return privateWishMapper.toAddPrivateWish();
    }


    // 팀원 공고 찜하기 메서드
    public TeamWishResponseDTO.AddTeamWish createWishToTeamProfile(final Long memberId, final Long teamMemberAnnouncementId) {
        final Member member = memberQueryAdapter.findById(memberId);
        final TeamMemberAnnouncement teamMemberAnnouncement = teamMemberAnnouncementRepository.findById(
                        teamMemberAnnouncementId)
                .orElseThrow(() -> new BadRequestException(NOT_FOUND_TEAM_MEMBER_ANNOUNCEMENT_ID));

        // 나의 팀 프로필의 ID -> 내가 찜한 팀원 공고의 팀 프로필과 같은지
        if (Objects.equals(getTeamProfile(memberId).getId(), teamMemberAnnouncement.getTeamProfile().getId())) {
            throw ForbiddenTeamWishException.EXCEPTION;
        }
        teamWishCommandAdapter.create(new TeamWish(null, member, teamMemberAnnouncement));
        member.addTeamWishCount();
        return teamWishMapper.toAddTeamWish();
    }

    // 찜하기 취소 메서드
    public PrivateWishResponseDTO.RemovePrivateWish cancelWishToPrivateProfile(final Long memberId, final Long profileId) {
        privateWishCommandAdapter.deleteByMemberIdAndProfileId(memberId, profileId);
        final Member member = memberQueryAdapter.findById(memberId);
        member.subPrivateWishCount();
        return privateWishMapper.toRemovePrivateWish();
    }

    // 팀원 공고 찜하기 취소 메서드
    public TeamWishResponseDTO.RemoveTeamWish cancelWishToTeamProfile(final Long memberId, final Long teamMemberAnnouncementId) {
        teamWishCommandAdapter.deleteByMemberIdAndTeamMemberAnnouncementId(memberId, teamMemberAnnouncementId);
        final Member member = memberQueryAdapter.findById(memberId);
        member.subTeamWishCount();
        return teamWishMapper.toRemoveTeamWish();
    }

    public List<BrowseMiniProfileResponse> getPrivateProfileWishList(final Long memberId) {
        final List<PrivateWish> privateWishList = privateWishQueryAdapter.findAllPrivateWish(memberId);
        return privateWishList.stream()
                .map(privateWish -> {
                    // 상대의 미니 프로필 객체를 가져온다.
                    final MiniProfile miniProfile = privateWish.getProfile().getMiniProfile();
                    final List<MiniProfileKeyword> miniProfileKeywords = getMiniProfileKeywords(miniProfile.getId());
                    // 상대 회원의 기본 정보를 가져와야 한다.
                    final MemberBasicInform memberBasicInform = memberBasicInformQueryAdapter.findByMemberId(memberId);
                    final List<String> jobRoleNames = getJobRoleNames(miniProfile.getProfile().getMember().getId());

                    // privateWish -> 찾아야함 (내가 이 해당 미니 프로필을 찜해뒀는지?)
                    final boolean isPrivateWish = privateWishRepository.existsByMemberIdAndProfileId(memberId,
                            miniProfile.getProfile().getId());

                    return BrowseMiniProfileResponse.personalBrowseMiniProfile(
                            miniProfile,
                            miniProfileKeywords,
                            memberBasicInform.getMemberName(),
                            jobRoleNames,
                            isPrivateWish
                    );
                })
                .toList();
    }


    // 찜한 상대 팀 (팀원 공고)를 찾아야 한다.
    public List<WishTeamProfileResponse> getTeamProfileWishList(final Long memberId) {
        final Member member = memberQueryAdapter.findById(memberId);
        final List<TeamWish> teamWishList = teamWishRepository.findAllByMemberId(member.getId());
        log.info("teamWishList={}", teamWishList);

        return teamWishList.stream().map(teamWish -> convertToWishTeamProfileResponse(teamWish, member.getId()))
                .toList();
    }

    // 팀 소개서 응답 변환
    private WishTeamProfileResponse convertToWishTeamProfileResponse(final TeamWish teamWish, final Long memberId) {

        // 팀원 공고를 조회한다.
        final TeamMemberAnnouncement teamMemberAnnouncement = teamWish.getTeamMemberAnnouncement();

        final TeamMiniProfile teamMiniProfile = getTeamMiniProfileByTeamProfileId(
                teamMemberAnnouncement.getTeamProfile().getId());
        final List<TeamMiniProfileKeyword> teamMiniProfileKeyword = teamMiniProfileKeywordRepository.findAllByTeamMiniProfileId(
                teamMiniProfile.getId());

        final TeamMemberAnnouncementJobRole teamMemberAnnouncementJobRole = getTeamMemberAnnouncementJobRole(
                teamMemberAnnouncement.getId());
        final List<TeamMemberAnnouncementSkill> teamMemberAnnouncementSkillList = getTeamMemberAnnouncementSkills(
                teamMemberAnnouncement.getId());
        final String teamName = teamMemberAnnouncement.getTeamProfile().getTeamMiniProfile().getTeamName();

        final boolean isTeamSaved = teamWishRepository.existsByTeamMemberAnnouncementIdAndMemberId(
                teamMemberAnnouncement.getId(), memberId);

        return new WishTeamProfileResponse(
                TeamMiniProfileResponse.personalTeamMiniProfile(teamMiniProfile, teamMiniProfileKeyword),
                TeamMemberAnnouncementResponse.afterLogin(teamMiniProfile.getTeamLogoImageUrl(), teamMemberAnnouncement,
                        teamName, teamMemberAnnouncementJobRole, teamMemberAnnouncementSkillList, isTeamSaved)
        );
    }

    public List<String> getJobRoleNames(final Long memberId) {
        final Profile profile = profileQueryAdapter.findByMemberId(memberId);
        final List<ProfileJobRole> profileJobRoleList = getProfileJobRoleList(profile.getId());

        List<JobRole> jobRoleList = profileJobRoleList.stream()
                .map(ProfileJobRole::getJobRole)
                .toList();

        return jobRoleList.stream()
                .map(JobRole::getJobRoleName)
                .toList();
    }

    private List<ProfileJobRole> getProfileJobRoleList(final Long profileId) {
        return profileJobRoleRepository.findAllByProfileId(profileId);
    }

    private TeamMemberAnnouncementJobRole getTeamMemberAnnouncementJobRole(final Long teamMemberAnnouncementId) {
        return teamMemberAnnouncementJobRoleRepository.findByTeamMemberAnnouncementId(teamMemberAnnouncementId)
                .orElseThrow(() -> new BadRequestException(NOT_FOUND_TEAM_MEMBER_ANNOUNCEMENT_JOB_ROLE));
    }

    private List<TeamMemberAnnouncementSkill> getTeamMemberAnnouncementSkills(final Long teamMemberAnnouncementId) {
        return teamMemberAnnouncementSkillRepository.findAllByTeamMemberAnnouncementId(teamMemberAnnouncementId);
    }

    private TeamMiniProfile getTeamMiniProfileByTeamProfileId(final Long teamProfileId) {
        return teamMiniProfileRepository.findByTeamProfileId(teamProfileId)
                .orElseThrow(() -> new BadRequestException(NOT_FOUND_TEAM_MINI_PROFILE_BY_TEAM_PROFILE_ID));
    }

    private List<MiniProfileKeyword> getMiniProfileKeywords(final Long miniProfileId) {
        return miniProfileKeywordRepository.findAllByMiniProfileId(miniProfileId);
    }


    // 내 이력서 찜하기 최대 개수 판단 메서드
    public void validateMemberMaxPrivateWish(final Long memberId) {
        final Member member = memberQueryAdapter.findById(memberId);
        final int memberPrivateWishCount = member.getPrivateWishCount();
        if (memberPrivateWishCount >= 8) {
            throw PrivateWishManyRequestException.EXCEPTION;
        }
    }

    // 팀 소개서 찜하기 최대 개수 판단 메서드
    public void validateMemberMaxTeamWish(final Long memberId) {
        final Member member = memberQueryAdapter.findById(memberId);
        final int memberTeamWishCount = member.getTeamWishCount();
        if (memberTeamWishCount >= 8) {
            throw TeamWishManyRequestException.EXCEPTION;
        }
    }
}
