package liaison.linkit.login.infrastructure.oauthUserInfo;

import static lombok.AccessLevel.PRIVATE;

import com.fasterxml.jackson.annotation.JsonProperty;
import liaison.linkit.login.domain.OauthUserInfo;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = PRIVATE)
@AllArgsConstructor
public class GoogleUserInfo implements OauthUserInfo {

    @JsonProperty("id")
    private String socialLoginId;

    @JsonProperty("email")
    private String email;

    public String getSocialLoginId() {
        return socialLoginId;
    }

    @Override
    public String getEmail() {
        return email;
    }
}
