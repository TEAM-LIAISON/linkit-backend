package liaison.linkit.login.infrastructure;

import static liaison.linkit.common.exception.AuthErrorCode.ACCESS_TOKEN_EXPIRED;
import static liaison.linkit.common.exception.AuthErrorCode.INVALID_ACCESS_TOKEN;
import static liaison.linkit.common.exception.AuthErrorCode.INVALID_REFRESH_TOKEN;
import static liaison.linkit.common.exception.AuthErrorCode.REFRESH_TOKEN_EXPIRED;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

import java.nio.charset.StandardCharsets;
import java.util.Date;

import io.jsonwebtoken.Header;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import liaison.linkit.common.exception.ExpiredAccessTokenException;
import liaison.linkit.common.exception.InvalidAccessTokenException;
import liaison.linkit.common.exception.InvalidRefreshTokenException;
import liaison.linkit.common.exception.RefreshTokenExpiredException;
import liaison.linkit.login.domain.MemberTokens;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@Transactional
class JwtProviderTest {

    private static final Long SAMPLE_EXPIRATION_TIME = 60000L;
    private static final Long SAMPLE_EXPIRED_TIME = 0L;
    private static final String SAMPLE_SUBJECT = "thisIsSampleSubject";
    private static final String SAMPLE_INVALID_SECRET_KEY =
            "LJW25,jjongwa,mcodnjs,hgo641,waterricecake,Let'sGo";

    @Value("${jwt.secret}")
    private String realSecretKey;

    @Autowired JwtProvider jwtProvider;

    private MemberTokens makeTestMemberTokens() {
        return jwtProvider.generateLoginToken(SAMPLE_SUBJECT);
    }

    private String makeTestJwt(
            final Long expirationTime, final String subject, final String secretKey) {
        final Date now = new Date();
        final Date validity = new Date(now.getTime() + expirationTime);

        return Jwts.builder()
                .setHeaderParam(Header.TYPE, Header.JWT_TYPE)
                .setSubject(subject)
                .setIssuedAt(now)
                .setExpiration(validity)
                .signWith(
                        Keys.hmacShaKeyFor(secretKey.getBytes(StandardCharsets.UTF_8)),
                        SignatureAlgorithm.HS256)
                .compact();
    }

    @DisplayName("accessToken과 refreshToken을 생성할 수 있다.")
    @Test
    void generateLoginToken() {
        // given
        final MemberTokens memberTokens = makeTestMemberTokens();

        // when & then
        assertThat(jwtProvider.getSubject(memberTokens.getAccessToken())).isEqualTo(SAMPLE_SUBJECT);
        assertThat(jwtProvider.getSubject(memberTokens.getRefreshToken())).isNull();
    }

    @DisplayName("accessToken과 refreshToken이 모두 유효한 토큰일 때 검증로직을 통과한다.")
    @Test
    void validateToken_Success() {
        // given
        final MemberTokens memberTokens = makeTestMemberTokens();

        // when & then
        assertDoesNotThrow(() -> jwtProvider.validateTokens(memberTokens));
    }

    @DisplayName("refreshToken의 기한이 만료되었을 때 예외 처리한다")
    @Test
    void validateToken_ExpiredPeriodRefreshToken() {
        // given
        final String refreshToken = makeTestJwt(SAMPLE_EXPIRED_TIME, SAMPLE_SUBJECT, realSecretKey);
        final String accessToken =
                makeTestJwt(SAMPLE_EXPIRATION_TIME, SAMPLE_SUBJECT, realSecretKey);
        final MemberTokens memberTokens = new MemberTokens(accessToken, refreshToken);

        // when & then
        assertThatThrownBy(() -> jwtProvider.validateTokens(memberTokens))
                .isInstanceOf(RefreshTokenExpiredException.class)
                .hasMessage(REFRESH_TOKEN_EXPIRED.getReason());
    }

    @DisplayName("refreshToken이 올바르지 않은 형식일 때 예외 처리한다")
    @Test
    void validateToken_InvalidRefreshToken() {
        // given
        final String accessToken =
                makeTestJwt(SAMPLE_EXPIRATION_TIME, SAMPLE_SUBJECT, realSecretKey);
        final String refreshToken =
                makeTestJwt(SAMPLE_EXPIRATION_TIME, SAMPLE_SUBJECT, SAMPLE_INVALID_SECRET_KEY);
        final MemberTokens memberTokens = new MemberTokens(accessToken, refreshToken);

        // when & then
        assertThatThrownBy(() -> jwtProvider.validateTokens(memberTokens))
                .isInstanceOf(InvalidRefreshTokenException.class)
                .hasMessage(INVALID_REFRESH_TOKEN.getReason());
    }

    @DisplayName("accessToken의 기한이 만료되었을 때 예외 처리한다")
    @Test
    void validateToken_ExpiredPeriodAccessToken() {
        // given
        final String refreshToken =
                makeTestJwt(SAMPLE_EXPIRATION_TIME, SAMPLE_SUBJECT, realSecretKey);
        final String accessToken = makeTestJwt(SAMPLE_EXPIRED_TIME, SAMPLE_SUBJECT, realSecretKey);
        final MemberTokens memberTokens = new MemberTokens(accessToken, refreshToken);

        // when & then
        assertThatThrownBy(() -> jwtProvider.validateTokens(memberTokens))
                .isInstanceOf(ExpiredAccessTokenException.class)
                .hasMessage(ACCESS_TOKEN_EXPIRED.getReason());
    }

    @DisplayName("accessToken이 올바르지 않은 형식일 때 예외 처리한다")
    @Test
    void validateToken_InvalidAccessToken() {
        // given
        final String accessToken =
                makeTestJwt(SAMPLE_EXPIRATION_TIME, SAMPLE_SUBJECT, SAMPLE_INVALID_SECRET_KEY);
        final String refreshToken =
                makeTestJwt(SAMPLE_EXPIRATION_TIME, SAMPLE_SUBJECT, realSecretKey);
        final MemberTokens memberTokens = new MemberTokens(accessToken, refreshToken);

        // when & then
        assertThatThrownBy(() -> jwtProvider.validateTokens(memberTokens))
                .isInstanceOf(InvalidAccessTokenException.class)
                .hasMessage(INVALID_ACCESS_TOKEN.getReason());
    }

    @DisplayName("refreshToken이 유효하고 accessToken의 유효기간이 지났다면 true를 반환한다.")
    @Test
    void isValidRefreshAndInvalidAccess() {
        // given
        final String refreshToken =
                makeTestJwt(SAMPLE_EXPIRATION_TIME, SAMPLE_SUBJECT, realSecretKey);
        final String accessToken = makeTestJwt(SAMPLE_EXPIRED_TIME, SAMPLE_SUBJECT, realSecretKey);

        // when & then
        assertThatThrownBy(
                        () -> jwtProvider.isValidRefreshAndInvalidAccess(refreshToken, accessToken))
                .isInstanceOf(ExpiredAccessTokenException.class)
                .hasMessage(ACCESS_TOKEN_EXPIRED.getReason());
    }

    @DisplayName("refreshToken이 올바르지 않은 형태이면 예외 처리한다.")
    @Test
    void isValidRefreshAndInvalidAccess_InvalidRefreshToken() {
        // given
        final String accessToken = makeTestJwt(SAMPLE_EXPIRED_TIME, SAMPLE_SUBJECT, realSecretKey);
        final String refreshToken =
                makeTestJwt(SAMPLE_EXPIRATION_TIME, SAMPLE_SUBJECT, SAMPLE_INVALID_SECRET_KEY);

        // when & then
        assertThatThrownBy(
                        () -> jwtProvider.isValidRefreshAndInvalidAccess(refreshToken, accessToken))
                .isInstanceOf(InvalidRefreshTokenException.class)
                .hasMessage(INVALID_REFRESH_TOKEN.getReason());
    }

    @DisplayName("accessToken이 올바르지 않은 형태이면 예외 처리한다.")
    @Test
    void isValidRefreshAndInvalidAccess_InvalidAccessToken() {
        // given
        final String refreshToken =
                makeTestJwt(SAMPLE_EXPIRATION_TIME, SAMPLE_SUBJECT, realSecretKey);
        final String accessToken =
                makeTestJwt(SAMPLE_EXPIRED_TIME, SAMPLE_SUBJECT, SAMPLE_INVALID_SECRET_KEY);

        // when
        boolean result = jwtProvider.isValidRefreshAndInvalidAccess(refreshToken, accessToken);

        // then
        assertThat(result).isTrue();
    }

    @DisplayName("refreshToken이 만료되어 있으면 예외 처리한다.")
    @Test
    void isValidRefreshAndInvalidAccess_ExpiredPeriodRefreshToken() {
        // given
        final String refreshToken = makeTestJwt(SAMPLE_EXPIRED_TIME, SAMPLE_SUBJECT, realSecretKey);
        final String accessToken =
                makeTestJwt(SAMPLE_EXPIRED_TIME, SAMPLE_SUBJECT, SAMPLE_INVALID_SECRET_KEY);

        // when & then
        assertThatThrownBy(
                        () -> jwtProvider.isValidRefreshAndInvalidAccess(refreshToken, accessToken))
                .isInstanceOf(RefreshTokenExpiredException.class)
                .hasMessage(REFRESH_TOKEN_EXPIRED.getReason());
    }
}
