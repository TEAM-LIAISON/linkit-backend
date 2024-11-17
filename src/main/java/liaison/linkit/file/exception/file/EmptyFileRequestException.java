package liaison.linkit.file.exception.file;

import liaison.linkit.common.exception.BaseCodeException;

public class EmptyFileRequestException extends BaseCodeException {
    public static final BaseCodeException EXCEPTION = new EmptyFileRequestException();

    private EmptyFileRequestException() {
        super(FileErrorCode.EMPTY_FILE_REQUEST);
    }
}
