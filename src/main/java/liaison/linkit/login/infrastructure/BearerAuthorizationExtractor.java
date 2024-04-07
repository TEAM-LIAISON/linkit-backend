package liaison.linkit.login.infrastructure;

import liaison.linkit.global.exception.InvalidJwtException;
import org.springframework.stereotype.Component;

import static liaison.linkit.global.exception.ExceptionCode.INVALID_ACCESS_TOKEN;

@Component
public class BearerAuthorizationExtractor {
    private static final String BEARER_TYPE = "BEARER";

    public String extractAccessToken(String header) {
        if (header != null && header.startsWith(BEARER_TYPE)) {
            return header.substring(BEARER_TYPE.length()).trim();
        }
        throw new InvalidJwtException(INVALID_ACCESS_TOKEN);
    }
}
