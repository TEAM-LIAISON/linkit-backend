package liaison.linkit.file.exception.image;

import liaison.linkit.common.exception.BaseCodeException;

public class ImageNameHashErrorException extends BaseCodeException {
    public static final BaseCodeException EXCEPTION = new ImageNameHashErrorException();

    private ImageNameHashErrorException() {
        super(ImageErrorCode.IMAGE_NAME_HASH_ERROR);
    }
}
