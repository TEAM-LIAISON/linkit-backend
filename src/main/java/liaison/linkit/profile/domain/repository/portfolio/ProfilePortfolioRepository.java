package liaison.linkit.profile.domain.repository.portfolio;

import liaison.linkit.profile.domain.portfolio.ProfilePortfolio;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProfilePortfolioRepository extends JpaRepository<ProfilePortfolio, Long>, ProfilePortfolioCustomRepository {
}
