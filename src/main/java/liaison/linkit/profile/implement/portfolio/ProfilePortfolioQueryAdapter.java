package liaison.linkit.profile.implement.portfolio;

import java.util.List;
import liaison.linkit.common.annotation.Adapter;
import liaison.linkit.profile.domain.portfolio.ProfilePortfolio;
import liaison.linkit.profile.domain.repository.portfolio.ProfilePortfolioRepository;
import liaison.linkit.profile.exception.portfolio.PortfolioNotFoundException;
import lombok.RequiredArgsConstructor;

@Adapter
@RequiredArgsConstructor
public class ProfilePortfolioQueryAdapter {

    private final ProfilePortfolioRepository profilePortfolioRepository;

    public ProfilePortfolio getProfilePortfolio(final Long profilePortfolioId) {
        return profilePortfolioRepository.getProfilePortfolio(profilePortfolioId)
                .orElseThrow(() -> PortfolioNotFoundException.EXCEPTION);
    }

    public List<ProfilePortfolio> getProfilePortfolios(final Long profileId) {
        return profilePortfolioRepository.getProfilePortfolios(profileId);
    }

}
