package liaison.linkit.login.dto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public class LoginResponse {
    private final String accessToken;
    private final String email;
    private final boolean existMemberBasicInform;
    private final boolean existDefaultProfile;
}
