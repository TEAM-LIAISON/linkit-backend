package liaison.linkit.file.exception.image;

import liaison.linkit.common.exception.BaseCodeException;

public class InvalidImagePathException extends BaseCodeException {
    public static final BaseCodeException EXCEPTION = new InvalidImagePathException();

    private InvalidImagePathException() {
        super(ImageErrorCode.INVALID_IMAGE_PATH);
    }
}
