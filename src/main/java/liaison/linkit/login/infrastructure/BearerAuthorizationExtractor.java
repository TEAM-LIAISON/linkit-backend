package liaison.linkit.login.infrastructure;

import liaison.linkit.common.exception.InvalidAccessTokenException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class BearerAuthorizationExtractor {
    private static final String BEARER_TYPE = "Bearer ";

    public String extractAccessToken(String header) {
        if (header != null && header.startsWith(BEARER_TYPE)) {
            return header.substring(BEARER_TYPE.length()).trim();
        }
        throw InvalidAccessTokenException.EXCEPTION;
    }
}
