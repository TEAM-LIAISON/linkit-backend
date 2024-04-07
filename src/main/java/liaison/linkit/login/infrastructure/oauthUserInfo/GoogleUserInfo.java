package liaison.linkit.login.infrastructure.oauthUserInfo;

import com.fasterxml.jackson.annotation.JsonProperty;
import liaison.linkit.login.domain.OauthUserInfo;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import static lombok.AccessLevel.PRIVATE;

@NoArgsConstructor(access = PRIVATE)
@AllArgsConstructor
public class GoogleUserInfo implements OauthUserInfo {

    @JsonProperty("id")
    private String socialLoginId;
    @JsonProperty("name")
    private String name;
    @JsonProperty("picture")
    private String picture;

    public String getSocialLoginId() {return socialLoginId;}

    @Override
    public String getNickname() {return name;}

    @Override
    public String getImageUrl() {return picture;}
}
