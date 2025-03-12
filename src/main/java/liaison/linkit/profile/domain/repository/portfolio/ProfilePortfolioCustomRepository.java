package liaison.linkit.profile.domain.repository.portfolio;

import java.util.List;
import java.util.Optional;

import liaison.linkit.profile.domain.portfolio.ProfilePortfolio;
import liaison.linkit.profile.presentation.portfolio.dto.ProfilePortfolioRequestDTO.UpdateProfilePortfolioRequest;

public interface ProfilePortfolioCustomRepository {
    List<ProfilePortfolio> getProfilePortfolios(final Long profileId);

    Optional<ProfilePortfolio> getProfilePortfolio(final Long profilePortfolioId);

    ProfilePortfolio updateProfilePortfolio(
            final ProfilePortfolio profilePortfolio,
            final UpdateProfilePortfolioRequest updateProfilePortfolioRequest);

    boolean existsByProfileId(final Long profileId);

    void removeProfilePortfoliosByProfileId(final Long profileId);
}
