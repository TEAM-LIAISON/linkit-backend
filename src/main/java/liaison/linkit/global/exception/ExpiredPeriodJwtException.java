package liaison.linkit.global.exception;

public class ExpiredPeriodJwtException extends AuthException{
    public ExpiredPeriodJwtException(final ExceptionCode exceptionCode) {
        super(exceptionCode);
    }
}
