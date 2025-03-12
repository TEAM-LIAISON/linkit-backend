package liaison.linkit.profile.implement.portfolio;

import java.util.List;

import liaison.linkit.common.annotation.Adapter;
import liaison.linkit.profile.domain.portfolio.ProjectLink;
import liaison.linkit.profile.domain.repository.portfolio.ProjectLinkRepository;
import lombok.RequiredArgsConstructor;

@Adapter
@RequiredArgsConstructor
public class ProjectLinkCommandAdapter {

    private final ProjectLinkRepository projectLinkRepository;

    public List<ProjectLink> addProjectLinks(final List<ProjectLink> projectLinks) {
        return projectLinkRepository.saveAll(projectLinks);
    }

    public void deleteAll(final List<ProjectLink> projectLinks) {
        projectLinkRepository.deleteAll(projectLinks);
    }
}
