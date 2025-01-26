package liaison.linkit.file.exception.image;

import liaison.linkit.common.exception.BaseCodeException;

public class EmptyImageRequestException extends BaseCodeException {
    public static final BaseCodeException EXCEPTION = new EmptyImageRequestException();

    private EmptyImageRequestException() {
        super(ImageErrorCode.EMPTY_IMAGE_REQUEST);
    }
}
