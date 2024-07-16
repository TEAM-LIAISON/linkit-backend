package liaison.linkit.global.exception;

import lombok.Getter;

@Getter
public class FileException extends BadRequestException{
    public FileException(final ExceptionCode exceptionCode) {
        super(exceptionCode);
    }
}
