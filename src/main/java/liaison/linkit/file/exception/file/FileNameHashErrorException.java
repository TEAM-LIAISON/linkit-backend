package liaison.linkit.file.exception.file;

import liaison.linkit.common.exception.BaseCodeException;

public class FileNameHashErrorException extends BaseCodeException {
    public static final BaseCodeException EXCEPTION = new FileNameHashErrorException();

    private FileNameHashErrorException() {
        super(FileErrorCode.FILE_NAME_HASH_ERROR);
    }
}
