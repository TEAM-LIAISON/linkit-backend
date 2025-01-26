package liaison.linkit.file.exception.image;

import liaison.linkit.common.exception.BaseCodeException;

public class InvalidImageException extends BaseCodeException {
    public static final BaseCodeException EXCEPTION = new InvalidImageException();

    private InvalidImageException() {
        super(ImageErrorCode.INVALID_IMAGE_REQUEST);
    }
}
