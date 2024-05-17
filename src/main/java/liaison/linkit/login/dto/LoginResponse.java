package liaison.linkit.login.dto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public class LoginResponse {
    private final String accessToken;
    private final boolean isMemberBasicInform;
    private final String email;
}
