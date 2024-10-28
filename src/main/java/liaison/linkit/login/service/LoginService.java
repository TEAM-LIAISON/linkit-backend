package liaison.linkit.login.service;

import static liaison.linkit.global.exception.ExceptionCode.DUPLICATED_EMAIL;
import static liaison.linkit.global.exception.ExceptionCode.FAIL_TO_GENERATE_MEMBER;
import static liaison.linkit.global.exception.ExceptionCode.INVALID_REFRESH_TOKEN;

import java.util.Optional;
import java.util.Random;
import liaison.linkit.global.exception.AuthException;
import liaison.linkit.login.business.AccountMapper;
import liaison.linkit.login.domain.MemberTokens;
import liaison.linkit.login.domain.OauthProvider;
import liaison.linkit.login.domain.OauthProviders;
import liaison.linkit.login.domain.OauthUserInfo;
import liaison.linkit.login.domain.RefreshToken;
import liaison.linkit.login.domain.repository.RefreshTokenRepository;
import liaison.linkit.login.infrastructure.BearerAuthorizationExtractor;
import liaison.linkit.login.infrastructure.JwtProvider;
import liaison.linkit.login.presentation.dto.AccountResponseDTO;
import liaison.linkit.mail.service.MailService;
import liaison.linkit.matching.domain.repository.privateMatching.PrivateMatchingRepository;
import liaison.linkit.matching.domain.repository.teamMatching.TeamMatchingRepository;
import liaison.linkit.member.domain.Member;
import liaison.linkit.member.domain.MemberBasicInform;
import liaison.linkit.member.domain.repository.memberBasicInform.MemberBasicInformRepository;
import liaison.linkit.member.implement.MemberBasicInformCommandAdapter;
import liaison.linkit.member.implement.MemberCommandAdapter;
import liaison.linkit.member.implement.MemberQueryAdapter;
import liaison.linkit.profile.domain.Profile;
import liaison.linkit.profile.domain.repository.profile.ProfileRepository;
import liaison.linkit.profile.implement.ProfileQueryAdapter;
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

    private final OauthProviders oauthProviders;
    private final JwtProvider jwtProvider;
    private final BearerAuthorizationExtractor bearerExtractor;

    private final AccountMapper accountMapper;

    private final MemberQueryAdapter memberQueryAdapter;
    private final MemberCommandAdapter memberCommandAdapter;
    private final MemberBasicInformCommandAdapter memberBasicInformCommandAdapter;


    private final RefreshTokenRepository refreshTokenRepository;
    private final ProfileRepository profileRepository;
    private final ProfileQueryAdapter profileQueryAdapter;

    private final MemberBasicInformRepository memberBasicInformRepository;
    private final PrivateMatchingRepository privateMatchingRepository;
    private final TeamMatchingRepository teamMatchingRepository;

    //    private final EmailReAuthenticationRedisUtil emailReAuthenticationRedisUtil;
    private final MailService mailService;

    // 회원이 로그인한다
    public AccountResponseDTO.LoginServiceResponse login(final String providerName, final String code) {
        final OauthProvider provider = oauthProviders.mapping(providerName);
        final OauthUserInfo oauthUserInfo = provider.getUserInfo(code);

        final Member member = findOrCreateMember(
                oauthUserInfo.getSocialLoginId(),
                oauthUserInfo.getEmail()
        );

        final boolean isMemberBasicInform = member.isCreateMemberBasicInform();

        // 토큰을 생성한다
        final MemberTokens memberTokens = jwtProvider.generateLoginToken(member.getId().toString());

        // 생성한 토큰 중 refresh-token을 레디스에 저장한다
        final RefreshToken savedRefreshToken = new RefreshToken(memberTokens.getRefreshToken(), member.getId());
        refreshTokenRepository.save(savedRefreshToken);

        return accountMapper.toLogin(
                memberTokens,
                oauthUserInfo.getEmail(),
                isMemberBasicInform
        );
    }

    private Member findOrCreateMember(final String socialLoginId, final String email) {
        final Optional<Member> member = memberQueryAdapter.findBySocialLoginId(socialLoginId);
        return member.orElseGet(() -> createMember(socialLoginId, email));
    }

    @Transactional
    public Member createMember(final String socialLoginId, final String email) {
        int tryCount = 0;
        while (tryCount < MAX_TRY_COUNT) {
            if (!memberQueryAdapter.existsByEmail(email)) {
                final Member member = memberCommandAdapter.create(new Member(socialLoginId, email, null));
                memberBasicInformCommandAdapter.create(new MemberBasicInform(
                        null, member, null, null, false, false, false, false
                ));
                return member;
            } else if (memberQueryAdapter.existsByEmail(email)) {
                throw new AuthException(DUPLICATED_EMAIL);
            }
            tryCount += 1;
        }
        throw new AuthException(FAIL_TO_GENERATE_MEMBER);
    }

    public AccountResponseDTO.RenewTokenResponse renewalAccessToken(
            final String refreshTokenRequest, final String authorizationHeader
    ) {
        // 기존에 사용하던 accessToken을 추출한다
        final String accessToken = bearerExtractor.extractAccessToken(authorizationHeader);
        return getRenewalToken(refreshTokenRequest);
    }

    private AccountResponseDTO.RenewTokenResponse getRenewalToken(final String refreshTokenRequest) {
        final RefreshToken refreshToken = refreshTokenRepository.findById(refreshTokenRequest)
                .orElseThrow(() -> new AuthException(INVALID_REFRESH_TOKEN));
        return accountMapper.toRenewTokenResponse(jwtProvider.regenerateAccessToken(refreshToken.getMemberId().toString()));
    }

    // 회원이 로그아웃한다
    public AccountResponseDTO.LogoutResponse logout(final Long memberId, final String refreshToken) {
        removeRefreshToken(refreshToken);
        return accountMapper.toLogout();
    }

    // 리프레시 토큰을 삭제한다
    public void removeRefreshToken(final String refreshToken) {
        refreshTokenRepository.deleteById(refreshToken);
    }

    // 회원이 서비스를 탈퇴한다
    public AccountResponseDTO.QuitAccountResponse quitAccount(final Long memberId) {
        final Member member = memberQueryAdapter.findById(memberId);
        return accountMapper.toQuitAccount();
    }

//    public EmailReAuthenticationResponse reAuthenticationEmail(
//            final Long memberId,
//            final EmailReAuthenticationRequest emailReAuthenticationRequest
//    ) throws MessagingException {
//
//        // 레디스에서 이메일 해시키가 존재한다면 데이터를 삭제한다. (5분 만료 이전에 다시 요청 보내는 경우 대비)
//        if (emailReAuthenticationRedisUtil.existData(emailReAuthenticationRequest.getEmail())) {
//            emailReAuthenticationRedisUtil.deleteData(emailReAuthenticationRequest.getEmail());
//        }
//
//        // 재인증 코드를 생성한다
//        final String authCode = createCode();
//
//        // 사용자가 입력한 이메일에 재인증 코드를 발송한다.
//        mailService.sendEmailReAuthenticationMail(emailReAuthenticationRequest.getEmail(), authCode);
//
//        // 재인증 코드를 발송한 시간 발행
//        return accountMapper.toReAuthenticationResponse();
//    }

    private String createCode() {
        int leftLimit = 48; // number '0'
        int rightLimit = 57; // number '9'
        int targetStringLength = 6;
        Random random = new Random();

        return random.ints(leftLimit, rightLimit + 1)
                .limit(targetStringLength)
                .collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append)
                .toString();
    }

    // 코드 검증
//    public Boolean verifyEmailCode(String email, String code) {
//        String codeFoundByEmail = emailReAuthenticationRedisUtil.getData(email);
//        log.info("code found by email: " + codeFoundByEmail);
//        if (codeFoundByEmail == null) {
//            return false;
//        }
//        return codeFoundByEmail.equals(code);
//    }


    // 수정 필요
    public void deleteAccount(final Long memberId) {
        final Member member = memberQueryAdapter.findById(memberId);
        final Profile profile = profileQueryAdapter.findByMemberId(memberId);

//        final List<TeamMemberAnnouncement> teamMemberAnnouncementList = getTeamMemberAnnouncementList(teamProfile);
//        final List<Long> teamMemberAnnouncementIds = teamMemberAnnouncementList.stream()
//                .map(TeamMemberAnnouncement::getId)
//                .toList();

        // 팀 매칭의 경우
        // 내가 어떤 팀 소개서에 매칭 요청 보낸 경우
        if (teamMatchingRepository.existsByMemberId(memberId)) {
            teamMatchingRepository.deleteByMemberId(memberId);
        }

        // 내가 올린 팀 소개서 (팀원 공고) 매칭 요청이 온 경우
//        if (teamMatchingRepository.existsByTeamMemberAnnouncementIds(teamMemberAnnouncementIds)) {
//            teamMatchingRepository.deleteByTeamMemberAnnouncementIds(teamMemberAnnouncementIds);
//        }

        // 내가 어떤 내 이력서에 매칭 요청 보낸 경우
        if (privateMatchingRepository.existsByMemberId(memberId)) {
            privateMatchingRepository.deleteByMemberId(memberId);
        }

        // 내가 올린 내 이력서에 매칭 요청이 온 경우
        if (privateMatchingRepository.existsByProfileId(profile.getId())) {
            privateMatchingRepository.deleteByProfileId(profile.getId());
        }

        // 내가 찜한 팀 소개서
//        if (teamScrapRepository.existsByMemberId(memberId)) {
//            teamScrapRepository.deleteByMemberId(memberId);
//        }

        // 나의 팀원 공고를 누가 찜한 경우
//        if (teamScrapRepository.existsByTeamMemberAnnouncementIds(teamMemberAnnouncementIds)) {
//            teamScrapRepository.deleteByTeamMemberAnnouncementIds(teamMemberAnnouncementIds);
//        }

        // 내가 찜한 내 이력서
//        if (privateScrapRepository.existsByMemberId(memberId)) {
//            privateScrapRepository.deleteByMemberId(memberId);
//        }
//
//        if (privateScrapRepository.existsByProfileId(profile.getId())) {
//            privateScrapRepository.deleteByProfileId(profile.getId());
//        }

//        if (memberBasicInformRepository.existsByMemberId(memberId)) {
//            memberBasicInformRepository.deleteByMemberId(memberId);
//        }

        // 회원가입하면 무조건 생기는 저장 데이터
        profileRepository.deleteByMemberId(memberId);
        memberCommandAdapter.deleteByMemberId(memberId);
    }
}
