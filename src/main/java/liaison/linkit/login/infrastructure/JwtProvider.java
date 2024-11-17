package liaison.linkit.login.infrastructure;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Header;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import javax.crypto.SecretKey;
import liaison.linkit.common.exception.ExpiredAccessTokenException;
import liaison.linkit.common.exception.InvalidAccessTokenException;
import liaison.linkit.common.exception.InvalidRefreshTokenException;
import liaison.linkit.common.exception.RefreshTokenExpiredException;
import liaison.linkit.login.domain.MemberTokens;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class JwtProvider {

    public static final String EMPTY_SUBJECT = "";

    private final SecretKey secretKey;
    private final Long accessExpirationTime;
    private final Long refreshExpirationTime;

    public JwtProvider(
            @Value("${jwt.secret}") final String secretKey,
            @Value("${jwt.access-expiration-time}") final Long accessExpirationTime,
            @Value("${jwt.refresh-expiration-time}") final Long refreshExpirationTime
    ) {
        try {
            this.secretKey = Keys.hmacShaKeyFor(secretKey.getBytes(StandardCharsets.UTF_8));
            this.accessExpirationTime = accessExpirationTime;
            this.refreshExpirationTime = refreshExpirationTime;
        } catch (Exception e) {
            // 로그를 통해 예외 내용 확인
            throw e;  // 여기서 재발생시켜 스택 트레이스를 확인할 수 있습니다.
        }
    }

    public MemberTokens generateLoginToken(final String subject) {
        final String accessToken = createToken(subject, accessExpirationTime);
        final String refreshToken = createToken(EMPTY_SUBJECT, refreshExpirationTime);
        return new MemberTokens(accessToken, refreshToken);
    }

    private String createToken(final String subject, final Long validityInMilliseconds) {
        final Date now = new Date();
        final Date validity = new Date(now.getTime() + validityInMilliseconds);

        return Jwts.builder()
                .setHeaderParam(Header.TYPE, Header.JWT_TYPE)
                .setSubject(subject)
                .setIssuedAt(now)
                .setExpiration(validity)
                .signWith(secretKey, SignatureAlgorithm.HS256)
                .compact();
    }

    public void validateTokens(final MemberTokens memberTokens) {
        final String refreshToken = memberTokens.getRefreshToken();
        final String accessToken = memberTokens.getAccessToken();

        try {
            validateRefreshToken(refreshToken);
            validateAccessToken(accessToken);
        } catch (RefreshTokenExpiredException e) {
            throw e;
        } catch (InvalidAccessTokenException e) {
            throw e;
        }
    }

    private void validateRefreshToken(final String refreshToken) {
        try {
            parseToken(refreshToken);
        } catch (final ExpiredJwtException e) {
            log.info("RefreshToken 만료 = {}", e.getMessage());
            throw RefreshTokenExpiredException.EXCEPTION;
        } catch (final JwtException | IllegalArgumentException e) {
            log.info("유효하지 않은 RefreshToken = {}", e.getMessage());
            throw InvalidRefreshTokenException.EXCEPTION;
        }
    }

    private void validateAccessToken(final String accessToken) {
        try {
            parseToken(accessToken);  // JWT 토큰을 파싱
        } catch (final ExpiredJwtException e) {
            log.info("AccessToken 만료 = {}", e.getMessage());
            throw ExpiredAccessTokenException.EXCEPTION;
        } catch (final JwtException | IllegalArgumentException e) {
            log.info("유효하지 않은 AccessToken = {}", e.getMessage());
            throw InvalidAccessTokenException.EXCEPTION;
        }
    }

    public String getSubject(final String token) {
        return parseToken(token)
                .getBody()
                .getSubject();
    }

    private Jws<Claims> parseToken(final String token) {
        return Jwts.parserBuilder()
                .setSigningKey(secretKey)
                .build()
                .parseClaimsJws(token);
    }

    public boolean isValidRefreshAndInvalidAccess(final String refreshToken, final String accessToken) {
        validateRefreshToken(refreshToken);
        try {
            validateAccessToken(accessToken);
        } catch (final InvalidAccessTokenException e) {
            return true;
        }
        return false;
    }

    public String regenerateAccessToken(final String subject) {
        return createToken(subject, accessExpirationTime);
    }

    public boolean isValidRefreshAndValidAccess(final String refreshToken, final String accessToken) {
        try {
            validateRefreshToken(refreshToken);
            validateAccessToken(accessToken);
            return true;
        } catch (final JwtException e) {
            return false;
        }
    }
}
