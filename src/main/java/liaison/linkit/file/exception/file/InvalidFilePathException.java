package liaison.linkit.file.exception.file;

import liaison.linkit.common.exception.BaseCodeException;

public class InvalidFilePathException extends BaseCodeException {
    public static final BaseCodeException EXCEPTION = new InvalidFilePathException();

    private InvalidFilePathException() {
        super(FileErrorCode.INVALID_FILE_PATH);
    }
}
