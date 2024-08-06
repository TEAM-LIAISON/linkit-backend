package liaison.linkit.login.dto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public class RenewTokenResponse {

    private final String accessToken;
    private final boolean existMemberBasicInform;
    private final boolean existDefaultProfile;
    private final boolean existNonCheckNotification;
    // true 경우 -> 확인하지 않은 알람이 있음
    // false 경우 -> 모든 알람을 확인한 상태
}
