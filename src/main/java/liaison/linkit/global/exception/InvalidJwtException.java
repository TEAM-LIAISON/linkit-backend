package liaison.linkit.global.exception;

public class InvalidJwtException extends AuthException{
    public InvalidJwtException(final ExceptionCode exceptionCode) {
        super(exceptionCode);
    }
}
