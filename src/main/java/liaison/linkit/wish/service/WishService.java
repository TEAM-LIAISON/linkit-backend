package liaison.linkit.wish.service;

import liaison.linkit.global.exception.BadRequestException;
import liaison.linkit.member.domain.Member;
import liaison.linkit.member.domain.MemberBasicInform;
import liaison.linkit.member.domain.repository.MemberBasicInformRepository;
import liaison.linkit.member.domain.repository.MemberRepository;
import liaison.linkit.profile.domain.Profile;
import liaison.linkit.profile.domain.miniProfile.MiniProfile;
import liaison.linkit.profile.domain.miniProfile.MiniProfileKeyword;
import liaison.linkit.profile.domain.repository.ProfileRepository;
import liaison.linkit.profile.domain.repository.jobRole.ProfileJobRoleRepository;
import liaison.linkit.profile.domain.repository.miniProfile.MiniProfileKeywordRepository;
import liaison.linkit.profile.domain.repository.miniProfile.MiniProfileRepository;
import liaison.linkit.profile.domain.role.JobRole;
import liaison.linkit.profile.domain.role.ProfileJobRole;
import liaison.linkit.profile.dto.response.miniProfile.MiniProfileResponse;
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
import liaison.linkit.wish.domain.PrivateWish;
import liaison.linkit.wish.domain.TeamWish;
import liaison.linkit.wish.domain.repository.PrivateWishRepository;
import liaison.linkit.wish.domain.repository.TeamWishRepository;
import liaison.linkit.wish.domain.type.WishType;
import liaison.linkit.wish.dto.response.WishTeamProfileResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

import static liaison.linkit.global.exception.ExceptionCode.*;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class WishService {

    private final MemberRepository memberRepository;
    private final ProfileRepository profileRepository;
    private final MiniProfileRepository miniProfileRepository;
    private final TeamMiniProfileRepository teamMiniProfileRepository;
    private final PrivateWishRepository privateWishRepository;
    private final TeamWishRepository teamWishRepository;
    private final MemberBasicInformRepository memberBasicInformRepository;
    private final ProfileJobRoleRepository profileJobRoleRepository;
    private final MiniProfileKeywordRepository miniProfileKeywordRepository;
    private final TeamMiniProfileKeywordRepository teamMiniProfileKeywordRepository;

    private final TeamMemberAnnouncementRepository teamMemberAnnouncementRepository;
    private final TeamMemberAnnouncementJobRoleRepository teamMemberAnnouncementJobRoleRepository;
    private final TeamMemberAnnouncementSkillRepository teamMemberAnnouncementSkillRepository;

    // 내 이력서 찜하기 최대 개수 판단 메서드
    public void validateMemberMaxPrivateWish(final Long memberId) {
        final Member member = getMember(memberId);
        final int memberPrivateWishCount = member.getPrivateWishCount();
        if (memberPrivateWishCount >= 8) {
            throw new BadRequestException(CANNOT_CREATE_PRIVATE_WISH_BECAUSE_OF_MAX_COUNT);
        }
    }

    // 팀 소개서 찜하기 최대 개수 판단 메서드
    public void validateMemberMaxTeamWish(final Long memberId) {
        final Member member = getMember(memberId);
        final int memberTeamWishCount = member.getTeamWishCount();
        if (memberTeamWishCount >= 8) {
            throw new BadRequestException(CANNOT_CREATE_TEAM_WISH_BECAUSE_OF_MAX_COUNT);
        }
    }

    // 회원 정보를 가져오는 메서드
    private Member getMember(final Long memberId) {
        return memberRepository.findById(memberId)
                .orElseThrow(() -> new BadRequestException(NOT_FOUND_MEMBER_BY_MEMBER_ID));
    }

    // 모든 "내 이력서" 서비스 계층에 필요한 profile 조회 메서드
    private Profile getProfile(final Long memberId) {
        return profileRepository.findByMemberId(memberId)
                .orElseThrow(() -> new BadRequestException(NOT_FOUND_PROFILE_BY_MEMBER_ID));
    }

    private Profile getProfileByMiniProfileId(
            final Long miniProfileId
    ) {
        final MiniProfile miniProfile = miniProfileRepository.findById(miniProfileId)
                .orElseThrow(() -> new BadRequestException(NOT_FOUND_MINI_PROFILE_BY_ID));

        return miniProfile.getProfile();
    }

    public void createWishToPrivateProfile(
            final Long memberId,
            final Long miniProfileId
    ) {
        // 내가
        final Member member = getMember(memberId);
        // 상대방의 내 이력서를
        final Profile profile = getProfileByMiniProfileId(miniProfileId);
        // 찜한다
        final PrivateWish privateWish = new PrivateWish(
                null,
                member,
                profile,
                WishType.PROFILE,
                LocalDateTime.now()
        );

        privateWishRepository.save(privateWish);
        // 내 이력서 찜하기 카운트 + 1
        member.addPrivateWishCount();
    }

    // 찜하기 취소 메서드
    public void cancelWishToPrivateProfile(
            final Long memberId,
            final Long miniProfileId
    ) {
        final Member member = getMember(memberId);
        final Profile profile = getProfileByMiniProfileId(miniProfileId);
        // 내 이력서 찜하기 객체 삭제
        privateWishRepository.deleteByMemberIdAndProfileId(memberId, profile.getId());
        member.subPrivateWishCount();
    }

    // 팀 소개서 찜하기 메서드
    public void createWishToTeamProfile(
            final Long memberId,
            final Long teamMemberAnnouncementId
    ) {
        final Member member = getMember(memberId);

        final TeamMemberAnnouncement teamMemberAnnouncement = teamMemberAnnouncementRepository.findById(teamMemberAnnouncementId)
                .orElseThrow(() -> new BadRequestException(NOT_FOUND_TEAM_MEMBER_ANNOUNCEMENT_ID));

        final TeamWish teamWish = new TeamWish(
                null,
                member,
                teamMemberAnnouncement,
                WishType.TEAM_PROFILE,
                LocalDateTime.now()
        );

        // DB에 저장
        teamWishRepository.save(teamWish);

        // 팀 소개서 찜하기 카운트 + 1
        member.addTeamWishCount();
    }

    public void cancelWishToTeamProfile(
            final Long memberId,
            final Long teamMemberAnnouncementId
    ) {
        final Member member = getMember(memberId);

        // 삭제하고자 하는 팀 찜하기 객체 조회
        final TeamWish teamWish = teamWishRepository.findByTeamMemberAnnouncementId(teamMemberAnnouncementId)
                .orElseThrow(() -> new BadRequestException(NOT_FOUND_TEAM_WISH_BY_TEAM_MEMBER_ANNOUNCEMENT_ID));

        // 바로 삭제
        teamWishRepository.delete(teamWish);

        // 삭제 이후 -1
        member.subTeamWishCount();
    }

    public List<MiniProfileResponse> getPrivateProfileWishList(final Long memberId) {
        final Member member = getMember(memberId);
        final List<PrivateWish> privateWishList = privateWishRepository.findAllByMemberId(member.getId());
        return privateWishList.stream()
                .map(privateWish -> {
                    final MiniProfile miniProfile = privateWish.getProfile().getMiniProfile();
                    final List<MiniProfileKeyword> miniProfileKeywords = getMiniProfileKeywords(miniProfile.getId());
                    final MemberBasicInform memberBasicInform = getMemberBasicInform(memberId);
                    final List<String> jobRoleNames = getJobRoleNames(memberId);
                    return MiniProfileResponse.personalMiniProfile(
                            miniProfile,
                            miniProfileKeywords,
                            memberBasicInform.getMemberName(),
                            jobRoleNames);
                })
                .toList();
    }


    public List<WishTeamProfileResponse> getTeamProfileWishList(final Long memberId) {
        final Member member = getMember(memberId);
        // 팀 찜 목록 객체 조회
        final List<TeamWish> teamWishList = teamWishRepository.findAllByMemberId(member.getId());
        log.info("teamWishList={}", teamWishList);

        return teamWishList.stream().map(this::convertToWishTeamProfileResponse).toList();
    }

    // 팀 소개서 응답 변환
    private WishTeamProfileResponse convertToWishTeamProfileResponse(final TeamWish teamWish) {
        final TeamMemberAnnouncement teamMemberAnnouncement = teamWish.getTeamMemberAnnouncement();

        final TeamMiniProfile teamMiniProfile = getTeamMiniProfileByTeamProfileId(teamMemberAnnouncement.getTeamProfile().getId());
        final List<TeamMiniProfileKeyword> teamMiniProfileKeyword = teamMiniProfileKeywordRepository.findAllByTeamMiniProfileId(teamMiniProfile.getId());

        final TeamMemberAnnouncementJobRole teamMemberAnnouncementJobRole = getTeamMemberAnnouncementJobRole(teamMemberAnnouncement.getId());
        final List<TeamMemberAnnouncementSkill> teamMemberAnnouncementSkillList = getTeamMemberAnnouncementSkills(teamMemberAnnouncement.getId());
        final String teamName = teamMemberAnnouncement.getTeamProfile().getTeamMiniProfile().getTeamName();

        return new WishTeamProfileResponse(
                TeamMiniProfileResponse.personalTeamMiniProfile(teamMiniProfile, teamMiniProfileKeyword),
                TeamMemberAnnouncementResponse.of(teamMemberAnnouncement, teamName, teamMemberAnnouncementJobRole, teamMemberAnnouncementSkillList)
        );
    }



    // 회원 기본 정보를 가져오는 메서드
    private MemberBasicInform getMemberBasicInform(final Long memberId) {
        return memberBasicInformRepository.findByMemberId(memberId)
                .orElseThrow(() -> new BadRequestException(NOT_FOUND_MEMBER_BASIC_INFORM_BY_MEMBER_ID));
    }

    public List<String> getJobRoleNames(final Long memberId) {
        final Profile profile = getProfile(memberId);
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
}
