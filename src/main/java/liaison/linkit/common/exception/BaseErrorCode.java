package liaison.linkit.common.exception;

public interface BaseErrorCode {

    ErrorReason getErrorReason();

    String getExplainError() throws NoSuchFieldException;
}
