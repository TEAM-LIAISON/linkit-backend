package liaison.linkit.profile.exception.position;

import liaison.linkit.common.exception.BaseCodeException;

public class PositionNotFoundException extends BaseCodeException {
    public static BaseCodeException EXCEPTION = new PositionNotFoundException();

    private PositionNotFoundException() {
        super(PositionErrorCode.POSITION_NOT_FOUND);
    }
}
