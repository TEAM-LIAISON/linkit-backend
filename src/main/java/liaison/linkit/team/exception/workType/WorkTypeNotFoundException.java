package liaison.linkit.team.exception.workType;

import liaison.linkit.common.exception.BaseCodeException;

public class WorkTypeNotFoundException extends BaseCodeException {
    public static WorkTypeNotFoundException EXCEPTION = new WorkTypeNotFoundException();

    private WorkTypeNotFoundException() {
        super(WorkTypeErrorCode.WORK_TYPE_NOT_FOUND);
    }
}
