package liaison.linkit.profile.domain.repository.portfolio;

import java.util.List;
import java.util.Optional;
import liaison.linkit.profile.domain.portfolio.ProfilePortfolio;

public interface ProfilePortfolioCustomRepository {
    List<ProfilePortfolio> getProfilePortfolios(final Long profileId);

    Optional<ProfilePortfolio> getProfilePortfolio(final Long profilePortfolioId);
}
