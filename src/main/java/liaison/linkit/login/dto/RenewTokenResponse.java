package liaison.linkit.login.dto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public class RenewTokenResponse {

    private final String accessToken;
    private final boolean existMemberBasicInform;

    private final boolean existNonCheckNotification;
}
