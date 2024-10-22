package liaison.linkit.login.dto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public class MemberTokensAndOnBoardingStepInform {

    private final String accessToken;
    private final String refreshToken;
    private final String email;
    private final boolean isMemberBasicInform;
}
