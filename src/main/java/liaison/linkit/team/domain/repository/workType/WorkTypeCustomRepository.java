package liaison.linkit.team.domain.repository.workType;

import java.util.Optional;

import liaison.linkit.team.domain.workType.WorkType;

public interface WorkTypeCustomRepository {

    Optional<WorkType> findByWorkTypeName(final String workTypeName);
}
