package liaison.linkit.image.infrastructure;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import liaison.linkit.global.exception.FileException;
import liaison.linkit.global.exception.ImageException;
import liaison.linkit.image.domain.ImageFile;
import liaison.linkit.image.domain.PortfolioFile;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStream;

import static liaison.linkit.global.exception.ExceptionCode.*;

@Component
@RequiredArgsConstructor
@Slf4j
public class S3Uploader {

    private static final String CACHE_CONTROL_VALUE = "max-age=3153600";

    private final AmazonS3 s3Client;

    // linkit-dev-env-bucket
    @Value("${cloud.aws.s3.bucket}")
    private String bucket;

    // images/
    @Value("${cloud.aws.s3.image-folder}")
    private String imageFolder;

    @Value("${cloud.aws.s3.file-folder}")
    private String fileFolder;

    @Value("${cloud.aws.s3.cloud-front-image-domain}")
    private String cloudFrontImageDomain;

    @Value("${cloud.aws.s3.cloud-front-file-domain}")
    private String cloudFrontFileDomain;

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

            String objectUrl = "https://" + cloudFrontImageDomain + "/" + path;


            log.info("Object URL = {}", objectUrl);

            return objectUrl;

        } catch (final AmazonServiceException e) {
            log.info("e={}", e);
            throw new ImageException(INVALID_IMAGE_PATH);
        } catch (final IOException e) {
            throw new ImageException(INVALID_IMAGE);
        }
    }

    public String uploadPortfolioFile(final PortfolioFile portfolioFile) {
        return uploadFile(portfolioFile);
    }

    private String uploadFile(final PortfolioFile portfolioFile) {
        // HashedName이 이름을 숨김.
        final String path = fileFolder + portfolioFile.getHashedName();
        log.info("path={}", path);

        final ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentType(portfolioFile.getContentType());
        metadata.setContentLength(portfolioFile.getSize());
        metadata.setCacheControl(CACHE_CONTROL_VALUE);

        try (final InputStream inputStream = portfolioFile.getInputStream()) {
            log.info("inputStream={}", inputStream);
            log.info("bucket={}", bucket);
            log.info("path={}", path);
            s3Client.putObject(bucket, path, inputStream, metadata);
            log.info("upload Successful");

            String objectUrl = "https://" + cloudFrontFileDomain + "/" + path;

            log.info("Object URL = {}", objectUrl);

            return objectUrl;

        } catch (final AmazonServiceException e) {
            log.info("e={}", e);
            throw new FileException(INVALID_FILE_PATH);
        } catch (final IOException e) {
            throw new FileException(INVALID_FILE);
        }
    }
}
