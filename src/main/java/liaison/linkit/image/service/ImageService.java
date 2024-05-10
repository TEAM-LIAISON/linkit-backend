package liaison.linkit.image.service;

import liaison.linkit.global.exception.ImageException;
import liaison.linkit.image.domain.ImageFile;
import liaison.linkit.image.domain.S3ImageEvent;
import liaison.linkit.image.domain.infrastructure.ImageUploader;
import liaison.linkit.image.dto.MiniProfileImageResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import static liaison.linkit.global.exception.ExceptionCode.EMPTY_IMAGE;
import static liaison.linkit.global.exception.ExceptionCode.EXCEED_IMAGE_SIZE;

@Service
@RequiredArgsConstructor
public class ImageService {

    private static final int MAX_IMAGE_SIZE = 1;
    private static final int EMPTY_SIZE = 0;

    private final ImageUploader imageUploader;
    private final ApplicationEventPublisher publisher;

    public MiniProfileImageResponse save(final MultipartFile miniProfileImageFile) {
        validateSizeOfImage(miniProfileImageFile);
        final ImageFile imageFile = new ImageFile(miniProfileImageFile);
        final String imageName = uploadMiniProfileImage(imageFile);
        return new MiniProfileImageResponse(imageName);
    }

    private String uploadMiniProfileImage(final ImageFile miniProfileImageFile) {
        try {
            return imageUploader.uploadMiniProfileImage(miniProfileImageFile);
        } catch (final ImageException e) {
            publisher.publishEvent(new S3ImageEvent(miniProfileImageFile.getHashedName()));
            throw e;
        }
    }

    private void validateSizeOfImage(final MultipartFile image) {
        if (image.getSize() > MAX_IMAGE_SIZE) {
            throw new ImageException(EXCEED_IMAGE_SIZE);
        }
        if (image.getSize() == EMPTY_SIZE) {
            throw new ImageException(EMPTY_IMAGE);
        }
    }
}
