package liaison.linkit.login.infrastructure.oauthprovider;

import static java.lang.Boolean.TRUE;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import liaison.linkit.common.exception.NotSupportedOauthServiceException;
import liaison.linkit.login.domain.OauthAccessToken;
import liaison.linkit.login.domain.OauthProvider;
import liaison.linkit.login.domain.OauthUserInfo;
import liaison.linkit.login.exception.AuthCodeBadRequestException;
import liaison.linkit.login.infrastructure.oauthUserInfo.KakaoUserInfo;
import liaison.linkit.member.domain.type.Platform;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

@Component
public class KakaoOauthProvider implements OauthProvider {
    private static final String PROVIDER_NAME = "kakao";
    private static final String SECURE_RESOURCE = "secure_resource";

    protected final String clientId;
    protected final String clientSecret;
    protected final String redirectUri;
    protected final String tokenUri;
    protected final String userUri;

    public KakaoOauthProvider(
            @Value("${spring.security.oauth2.client.registration.kakao.client-id}") final String clientId,
            @Value("${spring.security.oauth2.client.registration.kakao.client-secret}") final String clientSecret,
            @Value("${spring.security.oauth2.client.registration.kakao.redirect-uri}") final String redirectUri,
            @Value("${spring.security.oauth2.client.provider.kakao.token-uri}") final String tokenUri,
            @Value("${spring.security.oauth2.client.provider.kakao.user-info-uri}") final String userUri

    ) {
        this.clientId = clientId;
        this.clientSecret = clientSecret;
        this.redirectUri = redirectUri;
        this.tokenUri = tokenUri;
        this.userUri = userUri;
    }

    @Override
    public Platform getPlatform(final String providerName) {
        return Platform.KAKAO;
    }

    @Override
    public boolean is(final String name) {
        return PROVIDER_NAME.equals(name);
    }

    @Override
    public OauthUserInfo getUserInfo(final String code) {
        final String accessToken = requestAccessToken(code);
        final HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(accessToken);

        final HttpEntity<MultiValueMap<String, String>> userInfoRequestEntity = new HttpEntity<>(headers);

        final Map<String, Boolean> queryParam = new HashMap<>();
        queryParam.put(SECURE_RESOURCE, TRUE);

        final ResponseEntity<KakaoUserInfo> response = restTemplate.exchange(
                userUri,
                HttpMethod.GET,
                userInfoRequestEntity,
                KakaoUserInfo.class,
                queryParam
        );

        if (response.getStatusCode().is2xxSuccessful()) {
            return response.getBody();
        }
        throw NotSupportedOauthServiceException.EXCEPTION;
    }

    private String requestAccessToken(final String code) {
        final HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        final MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("code", code);
        params.add("client_id", clientId);
        params.add("client_secret", clientSecret);
        params.add("redirect_uri", redirectUri);
        params.add("grant_type", "authorization_code");
        
        final HttpEntity<MultiValueMap<String, String>> accessTokenRequestEntity = new HttpEntity<>(params, headers);

        final ResponseEntity<OauthAccessToken> accessTokenResponse = restTemplate.exchange(
                tokenUri,
                HttpMethod.POST,
                accessTokenRequestEntity,
                OauthAccessToken.class
        );

        return Optional.ofNullable(accessTokenResponse.getBody())
                .orElseThrow(() -> AuthCodeBadRequestException.EXCEPTION)
                .getAccessToken();
    }
}
