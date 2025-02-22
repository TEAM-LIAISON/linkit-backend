package liaison.linkit.profile.domain.repository.portfolio;

import java.util.List;

import liaison.linkit.profile.domain.portfolio.ProjectSubImage;

public interface ProjectSubImageCustomRepository {
    List<String> getProjectSubImagePaths(final Long profilePortfolioId);

    List<ProjectSubImage> getProjectSubImages(final Long profilePortfolioId);
}
