package liaison.linkit.global.exception;

import lombok.Getter;

@Getter
public class RefreshTokenException extends AuthException {
    public RefreshTokenException(final ExceptionCode exceptionCode) { super(exceptionCode);}
}
