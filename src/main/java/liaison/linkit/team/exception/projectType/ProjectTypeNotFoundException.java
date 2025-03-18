package liaison.linkit.team.exception.projectType;

import liaison.linkit.common.exception.BaseCodeException;

public class ProjectTypeNotFoundException extends BaseCodeException {
    public static ProjectTypeNotFoundException EXCEPTION = new ProjectTypeNotFoundException();

    private ProjectTypeNotFoundException() {
        super(ProjectTypeErrorCode.PROJECT_TYPE_NOT_FOUND);
    }
}
