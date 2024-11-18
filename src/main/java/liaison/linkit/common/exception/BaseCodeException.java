package liaison.linkit.common.exception;

public abstract class BaseCodeException extends RuntimeException {

    private final BaseErrorCode errorCode;

    public BaseCodeException(BaseErrorCode errorCode) {
        super(errorCode.getErrorReason().getReason());
        this.errorCode = errorCode;
    }

    public ErrorReason getErrorReason() {
        return this.errorCode.getErrorReason();
    }

    public BaseErrorCode getErrorCode() {
        return errorCode;
    }
}
