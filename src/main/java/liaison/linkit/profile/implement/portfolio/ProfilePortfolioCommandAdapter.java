package liaison.linkit.profile.implement.portfolio;

import liaison.linkit.common.annotation.Adapter;
import liaison.linkit.profile.domain.portfolio.ProfilePortfolio;
import liaison.linkit.profile.domain.repository.portfolio.ProfilePortfolioRepository;
import liaison.linkit.profile.presentation.portfolio.dto.ProfilePortfolioRequestDTO.UpdateProfilePortfolioRequest;
import lombok.RequiredArgsConstructor;

@Adapter
@RequiredArgsConstructor
public class ProfilePortfolioCommandAdapter {

    final ProfilePortfolioRepository profilePortfolioRepository;

    public ProfilePortfolio addProfilePortfolio(final ProfilePortfolio profilePortfolio) {
        return profilePortfolioRepository.save(profilePortfolio);
    }

    public ProfilePortfolio updateProfilePortfolio(final ProfilePortfolio profilePortfolio, final UpdateProfilePortfolioRequest updateProfilePortfolioRequest) {
        return profilePortfolioRepository.updateProfilePortfolio(profilePortfolio, updateProfilePortfolioRequest);
    }

    public void removeProfilePortfolio(final ProfilePortfolio profilePortfolio) {
        profilePortfolioRepository.delete(profilePortfolio);
    }

    public void removeProfilePortfoliosByProfileId(final Long profileId) {
        profilePortfolioRepository.removeProfilePortfoliosByProfileId(profileId);
    }
}
