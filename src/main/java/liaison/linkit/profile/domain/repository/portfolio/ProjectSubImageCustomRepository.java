package liaison.linkit.profile.domain.repository.portfolio;

import java.util.List;

public interface ProjectSubImageCustomRepository {
    List<String> getProjectSubImagePaths(final Long profilePortfolioId);
}
