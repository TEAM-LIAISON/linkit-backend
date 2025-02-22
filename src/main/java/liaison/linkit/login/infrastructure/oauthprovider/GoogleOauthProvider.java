package liaison.linkit.login.infrastructure.oauthprovider;

import java.util.Optional;

import liaison.linkit.common.exception.NotSupportedOauthServiceException;
import liaison.linkit.login.domain.OauthAccessToken;
import liaison.linkit.login.domain.OauthProvider;
import liaison.linkit.login.domain.OauthUserInfo;
import liaison.linkit.login.exception.AuthCodeBadRequestException;
import liaison.linkit.login.infrastructure.oauthUserInfo.GoogleUserInfo;
import liaison.linkit.member.domain.type.Platform;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

@Component
public class GoogleOauthProvider implements OauthProvider {
    private static final String PROVIDER_NAME = "google";

    protected final String clientId;
    protected final String clientSecret;
    protected final String redirectUri;
    protected final String tokenUri;
    protected final String userUri;

    public GoogleOauthProvider(
            @Value("${spring.security.oauth2.client.registration.google.client-id}")
                    final String clientId,
            @Value("${spring.security.oauth2.client.registration.google.client-secret}")
                    final String clientSecret,
            @Value("${spring.security.oauth2.client.registration.google.redirect-uri}")
                    final String redirectUri,
            @Value("${spring.security.oauth2.client.provider.google.token-uri}")
                    final String tokenUri, // Notice the path correction
            @Value("${spring.security.oauth2.client.provider.google.user-info-uri}")
                    final String userUri // Notice the path correction
            ) {
        this.clientId = clientId;
        this.clientSecret = clientSecret;
        this.redirectUri = redirectUri;
        this.tokenUri = tokenUri;
        this.userUri = userUri;
    }

    @Override
    public Platform getPlatform(final String providerName) {
        return Platform.GOOGLE;
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
        final HttpEntity<MultiValueMap<String, String>> userInfoRequestEntity =
                new HttpEntity<>(headers);

        final ResponseEntity<GoogleUserInfo> response =
                restTemplate.exchange(
                        userUri, HttpMethod.GET, userInfoRequestEntity, GoogleUserInfo.class);

        if (response.getStatusCode().is2xxSuccessful()) {
            return response.getBody();
        }
        throw NotSupportedOauthServiceException.EXCEPTION;
    }

    private String requestAccessToken(final String code) {
        final MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        final HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setBasicAuth(clientId, clientSecret);
        httpHeaders.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        params.add("code", code);
        params.add("client_id", clientId);
        params.add("client_secret", clientSecret);
        params.add("redirect_uri", redirectUri);
        params.add("grant_type", "authorization_code");

        final HttpEntity<MultiValueMap<String, String>> accessTokenRequestEntity =
                new HttpEntity<>(params, httpHeaders);
        final ResponseEntity<OauthAccessToken> accessTokenResponse =
                restTemplate.exchange(
                        tokenUri,
                        HttpMethod.POST,
                        accessTokenRequestEntity,
                        OauthAccessToken.class);

        return Optional.ofNullable(accessTokenResponse.getBody())
                .orElseThrow(() -> AuthCodeBadRequestException.EXCEPTION)
                .getAccessToken();
    }
}
