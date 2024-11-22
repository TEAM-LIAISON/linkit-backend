package liaison.linkit.profile.exception.portfolio;

import liaison.linkit.common.exception.BaseCodeException;
import liaison.linkit.profile.exception.position.PositionErrorCode;

public class PortfolioNotFoundException extends BaseCodeException {
    public static BaseCodeException EXCEPTION = new PortfolioNotFoundException();

    private PortfolioNotFoundException() {
        super(PositionErrorCode.POSITION_NOT_FOUND);
    }
}
