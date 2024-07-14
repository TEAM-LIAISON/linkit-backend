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
import liaison.linkit.team.domain.TeamProfile;
import liaison.linkit.team.domain.miniprofile.TeamMiniProfile;
import liaison.linkit.team.domain.miniprofile.TeamMiniProfileKeyword;
import liaison.linkit.team.domain.repository.miniprofile.TeamMiniProfileKeywordRepository;
import liaison.linkit.team.domain.repository.miniprofile.TeamMiniProfileRepository;
import liaison.linkit.team.dto.response.miniProfile.TeamMiniProfileResponse;
import liaison.linkit.wish.domain.PrivateWish;
import liaison.linkit.wish.domain.TeamWish;
import liaison.linkit.wish.domain.repository.PrivateWishRepository;
import liaison.linkit.wish.domain.repository.TeamWishRepository;
import liaison.linkit.wish.domain.type.WishType;
import liaison.linkit.wish.dto.response.MyWishResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Collections;
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

    private TeamProfile getTeamProfileByTeamMiniProfileId(
            final Long teamMiniProfileId
    ) {
        final TeamMiniProfile teamMiniProfile = teamMiniProfileRepository.findById(teamMiniProfileId)
                .orElseThrow(() -> new BadRequestException(NOT_FOUND_TEAM_MINI_PROFILE_ID));

        return teamMiniProfile.getTeamProfile();
    }

    public void createWishToPrivateProfile(
            final Long memberId,
            final Long miniProfileId
    ) {
        final Member member = getMember(memberId);
        final Profile profile = getProfileByMiniProfileId(miniProfileId);

        final PrivateWish privateWish = new PrivateWish(
                null,
                member,
                profile,
                WishType.PROFILE,
                LocalDateTime.now()
        );

        privateWishRepository.save(privateWish);
    }


    public void createWishToTeamProfile(
            final Long memberId,
            final Long teamMiniProfileId
    ) {
        final Member member = getMember(memberId);
        final TeamProfile teamProfile = getTeamProfileByTeamMiniProfileId(teamMiniProfileId);

        final TeamWish teamWish = new TeamWish(
                null,
                member,
                teamProfile,
                WishType.TEAM_PROFILE,
                LocalDateTime.now()
        );

        teamWishRepository.save(teamWish);
    }

    // 찜한 팀원, 찜한 팀 모두 반환 필요
    public Page<MyWishResponse> getMyWishList(final Long memberId, final Pageable pageable) {
        // 사용자 찾기
        final Member member = getMember(memberId);

        final List<PrivateWish> privateWishList = privateWishRepository.findAllByMemberId(member.getId());
        final List<MiniProfileResponse> miniProfileList = privateWishList.stream()
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
        // 팀 찜 목록
        final List<TeamWish> teamWishList = teamWishRepository.findAllByMemberId(member.getId());
        final List<TeamMiniProfileResponse> teamMiniProfileList = teamWishList.stream()
                .map(teamWish -> {
                    final TeamMiniProfile teamMiniProfile = teamWish.getTeamProfile().getTeamMiniProfile();
                    final List<TeamMiniProfileKeyword> teamMiniProfileKeyword = teamMiniProfileKeywordRepository.findAllByTeamMiniProfileId(teamMiniProfile.getId());
                    return TeamMiniProfileResponse.personalTeamMiniProfile(teamMiniProfile, teamMiniProfileKeyword);
                })
                .toList();

        final MyWishResponse myWishResponse = new MyWishResponse(miniProfileList, teamMiniProfileList);
        List<MyWishResponse> myWishResponseList = Collections.singletonList(myWishResponse);

        return new PageImpl<>(myWishResponseList, pageable, myWishResponseList.size());
    }

    private List<MiniProfileKeyword> getMiniProfileKeywords(final Long miniProfileId) {
        return miniProfileKeywordRepository.findAllByMiniProfileId(miniProfileId);
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
}
