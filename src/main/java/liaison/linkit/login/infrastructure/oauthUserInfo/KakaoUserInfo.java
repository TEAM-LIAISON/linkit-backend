package liaison.linkit.login.infrastructure.oauthUserInfo;

import static lombok.AccessLevel.PRIVATE;

import com.fasterxml.jackson.annotation.JsonProperty;
import liaison.linkit.login.domain.OauthUserInfo;
import lombok.NoArgsConstructor;

public class KakaoUserInfo implements OauthUserInfo {
    @JsonProperty("id")
    private String socialLoginId;

    @JsonProperty("kakao_account")
    private KakaoAccount kakaoAccount;

    @Override
    public String getSocialLoginId() {
        return socialLoginId;
    }

    @Override
    public String getEmail() {
        return kakaoAccount.email;
    }

    @NoArgsConstructor(access = PRIVATE)
    private static class KakaoAccount {
        @JsonProperty("email")
        private String email;
    }
}
