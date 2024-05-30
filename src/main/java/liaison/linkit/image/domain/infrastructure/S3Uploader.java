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

    @Value("${cloud.aws.s3.cloud-front-domain}")
    private String cloudFrontDomain;

    public String uploadMiniProfileImage(final ImageFile miniProfileImageFile) {
        return uploadImage(miniProfileImageFile);
    }

    private String uploadImage(final ImageFile imageFile) {
        // HashedName이 이름을 숨김.
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
            log.info("upload Successful");

            String objectUrl = "https://" + cloudFrontDomain + "/" + path;


            log.info("Object URL = {}", objectUrl);

            return objectUrl;

        } catch (final AmazonServiceException e) {
            log.info("e={}", e);
            throw new ImageException(INVALID_IMAGE_PATH);
        } catch (final IOException e) {
            throw new ImageException(INVALID_IMAGE);
        }
    }
}
