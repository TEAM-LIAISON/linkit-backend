package liaison.linkit.profile.implement.portfolio;

import java.util.List;
import liaison.linkit.common.annotation.Adapter;
import liaison.linkit.profile.domain.portfolio.ProjectSubImage;
import liaison.linkit.profile.domain.repository.portfolio.ProjectSubImageRepository;
import lombok.RequiredArgsConstructor;

@Adapter
@RequiredArgsConstructor
public class ProjectSubImageQueryAdapter {
    private final ProjectSubImageRepository projectSubImageRepository;

    public List<ProjectSubImage> getProjectSubImages(final Long profilePortfolioId) {
        return projectSubImageRepository.getProjectSubImages(profilePortfolioId);
    }

    public List<String> getProjectSubImagePaths(final Long profilePortfolioId) {
        return projectSubImageRepository.getProjectSubImagePaths(profilePortfolioId);
    }
}
