package liaison.linkit.notification.exception;

import liaison.linkit.common.exception.BaseCodeException;

public class NotificationNotFoundException extends BaseCodeException {
    public static BaseCodeException EXCEPTION = new NotificationNotFoundException();

    private NotificationNotFoundException() {
        super(NotificationErrorCode.NOTIFICATION_NOT_FOUND);
    }
}
