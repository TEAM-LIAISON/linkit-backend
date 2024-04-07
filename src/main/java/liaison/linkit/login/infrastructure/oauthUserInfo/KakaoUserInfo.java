package liaison.linkit.login.infrastructure.oauthUserInfo;

import com.fasterxml.jackson.annotation.JsonProperty;
import liaison.linkit.login.domain.OauthUserInfo;
import lombok.NoArgsConstructor;

import static lombok.AccessLevel.PRIVATE;

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
    public String getNickname() {
        return kakaoAccount.kakaoProfile.nickname;
    }

    @Override
    public String getImageUrl() {
        return kakaoAccount.kakaoProfile.image;
    }

    @NoArgsConstructor(access = PRIVATE)
    private static class KakaoAccount {
        @JsonProperty("profile")
        private KakaoProfile kakaoProfile;
    }

    @NoArgsConstructor(access = PRIVATE)
    private static class KakaoProfile {

        @JsonProperty("nickname")
        private String nickname;

        @JsonProperty("profile_image_url")
        private String image;
    }
}
