package liaison.linkit.profile.domain.repository.visit;

import liaison.linkit.profile.domain.visit.ProfileVisit;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProfileVisitRepository
        extends JpaRepository<ProfileVisit, Long>, ProfileVisitCustomRepository {}
