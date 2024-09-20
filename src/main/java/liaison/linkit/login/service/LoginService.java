package liaison.linkit.login.service;

import static liaison.linkit.global.exception.ExceptionCode.DUPLICATED_EMAIL;
import static liaison.linkit.global.exception.ExceptionCode.FAIL_TO_GENERATE_MEMBER;
import static liaison.linkit.global.exception.ExceptionCode.FAIL_TO_VALIDATE_TOKEN;
import static liaison.linkit.global.exception.ExceptionCode.INVALID_REFRESH_TOKEN;
import static liaison.linkit.global.exception.ExceptionCode.NOT_FOUND_PROFILE_BY_MEMBER_ID;
import static liaison.linkit.global.exception.ExceptionCode.NOT_FOUND_TEAM_PROFILE_BY_MEMBER_ID;

import jakarta.annotation.Nullable;
import java.util.List;
import java.util.Optional;
import liaison.linkit.global.exception.AuthException;
import liaison.linkit.global.exception.BadRequestException;
import liaison.linkit.login.domain.MemberTokens;
import liaison.linkit.login.domain.OauthProvider;
import liaison.linkit.login.domain.OauthProviders;
import liaison.linkit.login.domain.OauthUserInfo;
import liaison.linkit.login.domain.RefreshToken;
import liaison.linkit.login.domain.repository.RefreshTokenRepository;
import liaison.linkit.login.dto.MemberTokensAndOnBoardingStepInform;
import liaison.linkit.login.dto.RenewTokenResponse;
import liaison.linkit.login.infrastructure.BearerAuthorizationExtractor;
import liaison.linkit.login.infrastructure.JwtProvider;
import liaison.linkit.matching.domain.repository.privateMatching.PrivateMatchingRepository;
import liaison.linkit.matching.domain.repository.teamMatching.TeamMatchingRepository;
import liaison.linkit.member.domain.Member;
import liaison.linkit.member.domain.repository.memberBasicInform.MemberBasicInformRepository;
import liaison.linkit.member.implement.MemberCommandAdapter;
import liaison.linkit.member.implement.MemberQueryAdapter;
import liaison.linkit.profile.domain.Profile;
import liaison.linkit.profile.domain.repository.profile.ProfileRepository;
import liaison.linkit.team.domain.TeamProfile;
import liaison.linkit.team.domain.announcement.TeamMemberAnnouncement;
import liaison.linkit.team.domain.repository.announcement.TeamMemberAnnouncementRepository;
import liaison.linkit.team.domain.repository.teamProfile.TeamProfileRepository;
import liaison.linkit.wish.domain.repository.privateWish.PrivateWishRepository;
import liaison.linkit.wish.domain.repository.teamWish.TeamWishRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class LoginService {

    private static final int MAX_TRY_COUNT = 5;

    private final RefreshTokenRepository refreshTokenRepository;

    private final MemberQueryAdapter memberQueryAdapter;
    private final MemberCommandAdapter memberCommandAdapter;

    private final ProfileRepository profileRepository;
    private final TeamProfileRepository teamProfileRepository;
    private final OauthProviders oauthProviders;
    private final JwtProvider jwtProvider;
    private final BearerAuthorizationExtractor bearerExtractor;

    private final MemberBasicInformRepository memberBasicInformRepository;
    private final PrivateMatchingRepository privateMatchingRepository;
    private final TeamMatchingRepository teamMatchingRepository;
    private final PrivateWishRepository privateWishRepository;
    private final TeamWishRepository teamWishRepository;
    private final TeamMemberAnnouncementRepository teamMemberAnnouncementRepository;

    // 내 이력서 조회
    private Profile getProfileByMember(final Long memberId) {
        return profileRepository.findByMemberId(memberId)
                .orElseThrow(() -> new BadRequestException(NOT_FOUND_PROFILE_BY_MEMBER_ID));
    }

    // 팀 소개서 정보를 가져온다. (1개만 저장되어 있음)
    private TeamProfile getTeamProfile(final Long memberId) {
        return teamProfileRepository.findByMemberId(memberId)
                .orElseThrow(() -> new BadRequestException(NOT_FOUND_TEAM_PROFILE_BY_MEMBER_ID));
    }

    public MemberTokensAndOnBoardingStepInform login(final String providerName, final String code) {
        final OauthProvider provider = oauthProviders.mapping(providerName);
        final OauthUserInfo oauthUserInfo = provider.getUserInfo(code);

        // 소셜로그인 ID와 이메일 정보는 해당 플랫폼으로부터 가져옴
        final Member member = findOrCreateMember(
                oauthUserInfo.getSocialLoginId(),
                oauthUserInfo.getEmail()
        );

        // 멤버 테이블에서 기본 정보 입력 여부를 조회함
        final boolean existMemberBasicInform = member.isExistMemberBasicInform();
        log.info("loginService login method memberId={}", member.getId());

        log.info("저장된 profile 가져오기 시도");
        final Profile profile = getProfileByMember(member.getId());

        final TeamProfile teamProfile = getTeamProfile(member.getId());

        final boolean existDefaultPrivateProfile = profile.getExistDefaultPrivateProfile();
        log.info("existDefaultPrivateProfile={}", existDefaultPrivateProfile);

        final boolean existDefaultTeamProfile = teamProfile.getExistDefaultTeamProfile();
        log.info("existDefaultTeamProfile={}", existDefaultTeamProfile);

        final boolean existDefaultProfile = (existDefaultPrivateProfile || existDefaultTeamProfile);
        final MemberTokens memberTokens = jwtProvider.generateLoginToken(member.getId().toString());

        // 리프레시 토큰 저장
        final RefreshToken savedRefreshToken = new RefreshToken(memberTokens.getRefreshToken(), member.getId());
        refreshTokenRepository.save(savedRefreshToken);

        return new MemberTokensAndOnBoardingStepInform(
                memberTokens.getAccessToken(),
                memberTokens.getRefreshToken(),
                oauthUserInfo.getEmail(),
                existMemberBasicInform,
                existDefaultProfile
        );
    }

    private Member findOrCreateMember(final String socialLoginId, final String email) {
        final Optional<Member> member = memberQueryAdapter.findBySocialLoginId(socialLoginId);
        return member.orElseGet(() -> createMember(socialLoginId, email));
    }

    @Transactional
    public Member createMember(final String socialLoginId, final String email) {
        log.info("createMember 정상 실행");
        int tryCount = 0;
        while (tryCount < MAX_TRY_COUNT) {
            if (!memberQueryAdapter.existsByEmail(email)) {
                // 만약 이메일에 의해서 존재하지 않는 회원임이 판단된다면
                final Member member = memberCommandAdapter.create(new Member(socialLoginId, email, null));
                log.info("memberId={}", member.getId());

                // 내 이력서는 자동으로 생성된다. -> 미니 프로필도 함께 생성되어야 한다.
                final Profile savedProfile = profileRepository.save(new Profile(member, 0));
                log.info("savedProfile.ID={}", savedProfile.getId());

                final TeamProfile savedTeamProfile = teamProfileRepository.save(new TeamProfile(member, 0));
                log.info("savedTeamProfile.ID={}", savedTeamProfile.getId());

                return member;
            } else if (memberQueryAdapter.existsByEmail(email)) {
                throw new AuthException(DUPLICATED_EMAIL);
            }
            tryCount += 1;
        }
        throw new AuthException(FAIL_TO_GENERATE_MEMBER);
    }

    public RenewTokenResponse renewalAccessToken(
            final String refreshTokenRequest, final String authorizationHeader
    ) {
        // 기존의 엑세스 토큰 추출
        final String accessToken = bearerExtractor.extractAccessToken(authorizationHeader);

        // Refresh 유효, Access 유효 X
        if (jwtProvider.isValidRefreshAndInvalidAccess(refreshTokenRequest, accessToken)) {
            // Refresh -> 레디스에서 조회
            return getRenewTokenResponse(refreshTokenRequest);
            // Access 토큰 재생성
        }

        // Refresh, Access 모두 유효
        if (jwtProvider.isValidRefreshAndValidAccess(refreshTokenRequest, accessToken)) {
            return getRenewTokenResponse(refreshTokenRequest);
        }
        throw new AuthException(FAIL_TO_VALIDATE_TOKEN);
    }

    @Nullable
    private RenewTokenResponse getRenewTokenResponse(String refreshTokenRequest) {
        final RefreshToken refreshToken = refreshTokenRepository.findById(refreshTokenRequest)
                .orElseThrow(() -> new AuthException(INVALID_REFRESH_TOKEN));

        final Member member = memberQueryAdapter.findById(refreshToken.getMemberId());
        final boolean existMemberBasicInform = member.isExistMemberBasicInform();

        log.info("loginService login method memberId={}", member.getId());
        final Profile profile = getProfileByMember(member.getId());
        final TeamProfile teamProfile = getTeamProfile(member.getId());

        final boolean existDefaultPrivateProfile = profile.getExistDefaultPrivateProfile();
        log.info("existDefaultPrivateProfile={}", existDefaultPrivateProfile);

        final boolean existDefaultTeamProfile = teamProfile.getExistDefaultTeamProfile();
        log.info("existDefaultTeamProfile={}", existDefaultTeamProfile);

        final boolean existDefaultProfile = (existDefaultPrivateProfile || existDefaultTeamProfile);

        final List<TeamMemberAnnouncement> teamMemberAnnouncementList = getTeamMemberAnnouncementList(teamProfile);
        final List<Long> teamMemberAnnouncementIds = teamMemberAnnouncementList.stream()
                .map(TeamMemberAnnouncement::getId)
                .toList();

        // 2개 모두 false -> existNonCheckNotification -> true -> 알림 기능 형태 제공
        // 내가 보내거나 받은 내 이력서 관련 매칭 요청에서 check -> false인 것이 존재하는지

        // 내가 보내거나 받은 팀 소개서 관련 매칭 요청에서 check -> false인 것이 존재하는지
        final boolean existNonCheckNotification = (
                (privateMatchingRepository.existsNonCheckByMemberId(member.getId(), profile.getId()))
                        || (teamMatchingRepository.existsNonCheckByMemberId(member.getId(),
                        teamMemberAnnouncementIds)));

        if (existMemberBasicInform && existDefaultProfile) {
            return new RenewTokenResponse(jwtProvider.regenerateAccessToken(refreshToken.getMemberId().toString()),
                    existMemberBasicInform, existDefaultProfile, existNonCheckNotification);
        } else {
            return new RenewTokenResponse(null, existMemberBasicInform, existDefaultProfile, existNonCheckNotification);
        }
    }


    public void removeRefreshToken(final String refreshToken) {
        refreshTokenRepository.deleteById(refreshToken);
    }

    // 수정 필요
    public void deleteAccount(final Long memberId) {
        final Member member = memberQueryAdapter.findById(memberId);
        final Profile profile = getProfileByMember(memberId);
        final TeamProfile teamProfile = getTeamProfile(memberId);
        final List<TeamMemberAnnouncement> teamMemberAnnouncementList = getTeamMemberAnnouncementList(teamProfile);
        final List<Long> teamMemberAnnouncementIds = teamMemberAnnouncementList.stream()
                .map(TeamMemberAnnouncement::getId)
                .toList();

        // 팀 매칭의 경우
        // 내가 어떤 팀 소개서에 매칭 요청 보낸 경우
        if (teamMatchingRepository.existsByMemberId(memberId)) {
            teamMatchingRepository.deleteByMemberId(memberId);
        }

        // 내가 올린 팀 소개서 (팀원 공고) 매칭 요청이 온 경우
        if (teamMatchingRepository.existsByTeamMemberAnnouncementIds(teamMemberAnnouncementIds)) {
            teamMatchingRepository.deleteByTeamMemberAnnouncementIds(teamMemberAnnouncementIds);
        }

        // 내가 어떤 내 이력서에 매칭 요청 보낸 경우
        if (privateMatchingRepository.existsByMemberId(memberId)) {
            privateMatchingRepository.deleteByMemberId(memberId);
        }

        // 내가 올린 내 이력서에 매칭 요청이 온 경우
        if (privateMatchingRepository.existsByProfileId(profile.getId())) {
            privateMatchingRepository.deleteByProfileId(profile.getId());
        }

        // 내가 찜한 팀 소개서
        if (teamWishRepository.existsByMemberId(memberId)) {
            teamWishRepository.deleteByMemberId(memberId);
        }

        // 나의 팀원 공고를 누가 찜한 경우
        if (teamWishRepository.existsByTeamMemberAnnouncementIds(teamMemberAnnouncementIds)) {
            teamWishRepository.deleteByTeamMemberAnnouncementIds(teamMemberAnnouncementIds);
        }

        // 내가 찜한 내 이력서
        if (privateWishRepository.existsByMemberId(memberId)) {
            privateWishRepository.deleteByMemberId(memberId);
        }

        if (privateWishRepository.existsByProfileId(profile.getId())) {
            privateWishRepository.deleteByProfileId(profile.getId());
        }

        if (memberBasicInformRepository.existsByMemberId(memberId)) {
            memberBasicInformRepository.deleteByMemberId(memberId);
        }

        // 회원가입하면 무조건 생기는 저장 데이터
        teamProfileRepository.deleteByMemberId(memberId);
        profileRepository.deleteByMemberId(memberId);
        memberCommandAdapter.deleteByMemberId(memberId);
    }

    private List<TeamMemberAnnouncement> getTeamMemberAnnouncementList(final TeamProfile teamProfile) {
        return teamMemberAnnouncementRepository.findAllByTeamProfileId(teamProfile.getId());
    }


}
