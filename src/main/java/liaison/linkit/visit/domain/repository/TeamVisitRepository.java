package liaison.linkit.visit.domain.repository;

import liaison.linkit.visit.domain.TeamVisit;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TeamVisitRepository
        extends JpaRepository<TeamVisit, Long>, TeamVisitCustomRepository {}
