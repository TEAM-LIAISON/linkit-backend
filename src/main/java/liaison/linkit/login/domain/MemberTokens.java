package liaison.linkit.login.domain;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public class MemberTokens {
    private final String accessToken;
    private final String refreshToken;
}
