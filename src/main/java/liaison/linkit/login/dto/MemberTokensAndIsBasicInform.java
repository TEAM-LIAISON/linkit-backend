package liaison.linkit.login.dto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public class MemberTokensAndIsBasicInform {
    private final String accessToken;
    private final String refreshToken;
    private final boolean isMemberBasicInform;

    public boolean getIsMemberBasicInform() {
        return this.isMemberBasicInform;
    }
}
