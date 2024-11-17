package liaison.linkit.common.validator;

import liaison.linkit.file.exception.image.InvalidImageException;
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

    private void validateSizeOfImage(final MultipartFile image) {
        if (image == null || image.isEmpty()) {
            throw InvalidImageException.EXCEPTION;
        }
    }
}
