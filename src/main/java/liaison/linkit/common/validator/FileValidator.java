package liaison.linkit.common.validator;

import liaison.linkit.file.exception.file.EmptyFileRequestException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Component
@RequiredArgsConstructor
@Transactional
public class FileValidator {

    public boolean validatingFileUpload(final MultipartFile multipartFile) {
        if (multipartFile != null) {
            validateSizeOfFile(multipartFile);
            return true;
        }
        return false;
    }

    private void validateSizeOfFile(final MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw EmptyFileRequestException.EXCEPTION;
        }
    }
}
