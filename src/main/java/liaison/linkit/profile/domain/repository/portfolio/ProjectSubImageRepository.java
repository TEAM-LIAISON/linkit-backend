package liaison.linkit.profile.domain.repository.portfolio;

import liaison.linkit.profile.domain.portfolio.ProjectSubImage;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProjectSubImageRepository
        extends JpaRepository<ProjectSubImage, Long>, ProjectSubImageCustomRepository {}
