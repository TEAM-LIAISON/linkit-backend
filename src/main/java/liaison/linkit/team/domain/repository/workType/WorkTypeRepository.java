package liaison.linkit.team.domain.repository.workType;

import liaison.linkit.team.domain.workType.WorkType;
import org.springframework.data.jpa.repository.JpaRepository;

public interface WorkTypeRepository
        extends JpaRepository<WorkType, Long>, WorkTypeCustomRepository {}
