package liaison.linkit.file.exception.file;

import liaison.linkit.common.exception.BaseCodeException;

public class InvalidFileException extends BaseCodeException {
    public static final BaseCodeException EXCEPTION = new InvalidFileException();

    private InvalidFileException() {
        super(FileErrorCode.INVALID_FILE_REQUEST);
    }
}
