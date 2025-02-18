package liaison.linkit.profile.domain.repository.portfolio;

import java.util.List;
import liaison.linkit.profile.domain.portfolio.ProjectLink;

public interface ProjectLinkCustomRepository {

    List<ProjectLink> getProjectLinks(final Long profilePortfolioId);
}
