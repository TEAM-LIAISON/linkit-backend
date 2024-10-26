package liaison.linkit.common.validator;

import static liaison.linkit.global.exception.ExceptionCode.EMPTY_IMAGE;

import liaison.linkit.global.exception.ImageException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Component
@RequiredArgsConstructor
@Transactional
public class ImageValidator {

    public boolean validatingImageUpload(final MultipartFile multipartFile) {
        if (multipartFile != null) {
            validateSizeOfImage(multipartFile);
            return true;
        }
        return false;
    }

    public void validateSizeOfImage(final MultipartFile image) {
        if (image == null || image.isEmpty()) {
            throw new ImageException(EMPTY_IMAGE);
        }
    }
}
