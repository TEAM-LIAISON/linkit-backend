package liaison.linkit.login.infrastructure;

import static liaison.linkit.global.exception.ExceptionCode.INVALID_ACCESS_TOKEN;

import liaison.linkit.global.exception.InvalidJwtException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class BearerAuthorizationExtractor {
    private static final String BEARER_TYPE = "Bearer ";

    public String extractAccessToken(String header) {
        log.info("header={}", header);
        if (header != null && header.startsWith(BEARER_TYPE)) {
            log.info("accessToken ={}", header.substring(BEARER_TYPE.length()).trim());
            return header.substring(BEARER_TYPE.length()).trim();
        }
        throw new InvalidJwtException(INVALID_ACCESS_TOKEN);
    }
}
