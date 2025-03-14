package liaison.linkit.visit.domain.repository;

import liaison.linkit.visit.domain.ProfileVisit;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProfileVisitRepository
        extends JpaRepository<ProfileVisit, Long>, ProfileVisitCustomRepository {}
