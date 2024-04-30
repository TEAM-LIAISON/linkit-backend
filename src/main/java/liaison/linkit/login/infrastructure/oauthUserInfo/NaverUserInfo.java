package liaison.linkit.login.infrastructure.oauthUserInfo;

import com.fasterxml.jackson.annotation.JsonProperty;
import liaison.linkit.login.domain.OauthUserInfo;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
@AllArgsConstructor
public class NaverUserInfo implements OauthUserInfo {

    @JsonProperty("id")
    private String socialLoginId;
    @JsonProperty("email")
    private String email;

    public String getSocialLoginId() {return socialLoginId;}

    @Override
    public String getEmail() {
        return email;
    }

}
