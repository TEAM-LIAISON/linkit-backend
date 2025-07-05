package liaison.linkit.profile.implement.portfolio;

import java.util.List;

import liaison.linkit.common.annotation.Adapter;
import liaison.linkit.profile.domain.portfolio.ProjectSubImage;
import liaison.linkit.profile.domain.repository.portfolio.ProjectSubImageRepository;
import lombok.RequiredArgsConstructor;

@Adapter
@RequiredArgsConstructor
public class ProjectSubImageCommandAdapter {
    private final ProjectSubImageRepository projectSubImageRepository;

    public void saveAll(final List<ProjectSubImage> projectSubImages) {
        projectSubImageRepository.saveAll(projectSubImages);
    }

    public void deleteAll(final List<ProjectSubImage> projectSubImages) {
        projectSubImageRepository.deleteAll(projectSubImages);
    }

    public void delete(final ProjectSubImage projectSubImage) {
        projectSubImageRepository.delete(projectSubImage);
    }
}
