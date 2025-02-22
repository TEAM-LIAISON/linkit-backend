package liaison.linkit.login.domain;

import java.util.List;

import liaison.linkit.common.exception.NotSupportedOauthServiceException;
import org.springframework.stereotype.Component;

@Component
public class OauthProviders {
    private final List<OauthProvider> providers;

    public OauthProviders(final List<OauthProvider> providers) {
        this.providers = providers;
    }

    public OauthProvider mapping(final String providerName) {
        return providers.stream()
                .filter(provider -> provider.is(providerName))
                .findFirst()
                .orElseThrow(() -> NotSupportedOauthServiceException.EXCEPTION);
    }
}
