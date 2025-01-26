package liaison.linkit.team.exception.scale;

import liaison.linkit.common.exception.BaseCodeException;

public class ScaleNotFoundException extends BaseCodeException {
    public static BaseCodeException EXCEPTION = new ScaleNotFoundException();

    private ScaleNotFoundException() {
        super(ScaleErrorCode.SCALE_NOT_FOUND);
    }
}
