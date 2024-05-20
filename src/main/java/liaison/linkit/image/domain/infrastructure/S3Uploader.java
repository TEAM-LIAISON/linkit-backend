package liaison.linkit.image.domain.infrastructure;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import liaison.linkit.global.exception.ImageException;
import liaison.linkit.image.domain.ImageFile;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStream;

import static liaison.linkit.global.exception.ExceptionCode.INVALID_IMAGE;
import static liaison.linkit.global.exception.ExceptionCode.INVALID_IMAGE_PATH;

@Component
@RequiredArgsConstructor
@Slf4j
public class S3Uploader {

    private static final String CACHE_CONTROL_VALUE = "max-age=3153600";

    private final AmazonS3 s3Client;

    @Value("${cloud.aws.s3.bucket}")
    private String bucket;

    @Value("${cloud.aws.s3.image-folder}")
    private String imageFolder;

    public String uploadMiniProfileImage(final ImageFile miniProfileImageFile) {
        log.info("S3 bucket={}", bucket);
        log.info("S3 imageFolder={}", imageFolder);

        return uploadImage(miniProfileImageFile);
    }

    private String uploadImage(final ImageFile imageFile) {
        final String path = imageFolder + imageFile.getHashedName();
        log.info("path={}", path);

        final ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentType(imageFile.getContentType());
        metadata.setContentLength(imageFile.getSize());
        metadata.setCacheControl(CACHE_CONTROL_VALUE);

        try (final InputStream inputStream = imageFile.getInputStream()) {
            log.info("inputStream={}", inputStream);
            log.info("bucket={}", bucket);
            log.info("path={}", path);
            s3Client.putObject(bucket, path, inputStream, metadata);
        } catch (final AmazonServiceException e) {
            log.info("e={}", e);
            throw new ImageException(INVALID_IMAGE_PATH);
        } catch (final IOException e) {
            throw new ImageException(INVALID_IMAGE);
        }

        return imageFile.getHashedName();
    }

//    private String uploadImage(final ImageFile imageFile) {
//        // Ensure the imageFolder ends with a "/" to correctly form the path
//        final String path = imageFolder.endsWith("/") ? imageFolder : imageFolder + "/";
//        final String fullPath = path + imageFile.getHashedName(); // Construct the full path with the image name
//
//        log.info("Full path={}", fullPath);
//
//        final ObjectMetadata metadata = new ObjectMetadata();
//        metadata.setContentType(imageFile.getContentType());
//        metadata.setContentLength(imageFile.getSize());
//        metadata.setCacheControl(CACHE_CONTROL_VALUE);
//
//        try (final InputStream inputStream = imageFile.getInputStream()) {
//            // Detailed logging for debugging
//            log.info("Attempting to upload to bucket: {}, Path: {}", bucket, fullPath);
//            s3Client.putObject(bucket, fullPath, inputStream, metadata);
//            log.info("Upload successful");
//        } catch (final AmazonServiceException e) {
//            log.error("AWS error during file upload: {}, Error Message: {}", e.getErrorCode(), e.getMessage());
//            throw new ImageException(INVALID_IMAGE_PATH);
//        } catch (final IOException e) {
//            log.error("IO Exception: {}", e.getMessage());
//            throw new ImageException(INVALID_IMAGE);
//        }
//
//        return imageFile.getHashedName();
//    }

}
