package liaison.linkit.team.implement.workType;

import liaison.linkit.common.annotation.Adapter;
import liaison.linkit.team.domain.repository.workType.WorkTypeRepository;
import liaison.linkit.team.domain.workType.WorkType;
import liaison.linkit.team.exception.workType.WorkTypeNotFoundException;
import lombok.RequiredArgsConstructor;

@Adapter
@RequiredArgsConstructor
public class WorkTypeQueryAdapter {

    private final WorkTypeRepository workTypeRepository;

    public WorkType findByWorkTypeName(final String workTypeName) {
        return workTypeRepository
                .findByWorkTypeName(workTypeName)
                .orElseThrow(() -> WorkTypeNotFoundException.EXCEPTION);
    }
}
