package liaison.linkit.image.domain.infrastructure;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import liaison.linkit.global.exception.ImageException;
import liaison.linkit.image.domain.ImageFile;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStream;

import static liaison.linkit.global.exception.ExceptionCode.INVALID_IMAGE;
import static liaison.linkit.global.exception.ExceptionCode.INVALID_IMAGE_PATH;

@Component
@RequiredArgsConstructor
public class ImageUploader {

    private static final String CACHE_CONTROL_VALUE = "max-age=3153600";

    private final AmazonS3 s3Client;

    @Value("${AWS_S3_BUCKET_NAME}")
    private String bucket;

    private String folder;

    public String uploadMiniProfileImage(final ImageFile miniProfileImageFile) {
        return uploadImage(miniProfileImageFile);
    }

    private String uploadImage(final ImageFile imageFile) {
        final String path = folder + imageFile.getHashedName();
        final ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentType(imageFile.getContentType());
        metadata.setContentLength(imageFile.getSize());
        metadata.setCacheControl(CACHE_CONTROL_VALUE);

        try (final InputStream inputStream = imageFile.getInputStream()) {
            s3Client.putObject(bucket, path, inputStream, metadata);
        } catch (final AmazonServiceException e) {
            throw new ImageException(INVALID_IMAGE_PATH);
        } catch (final IOException e) {
            throw new ImageException(INVALID_IMAGE);
        }
        return imageFile.getHashedName();
    }
}
