package liaison.linkit.file.infrastructure;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.DeleteObjectRequest;
import com.amazonaws.services.s3.model.ObjectMetadata;
import jakarta.validation.constraints.NotNull;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import liaison.linkit.file.domain.CertificationFile;
import liaison.linkit.file.domain.ImageFile;
import liaison.linkit.file.exception.file.InvalidFileException;
import liaison.linkit.file.exception.file.InvalidFilePathException;
import liaison.linkit.file.exception.image.InvalidImageException;
import liaison.linkit.file.exception.image.InvalidImagePathException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

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

    @Value("${cloud.aws.s3.file-folder}")
    private String fileFolder;

    @Value("${cloud.aws.s3.cloud-front-image-domain}")
    private String cloudFrontImageDomain;

    @Value("${cloud.aws.s3.cloud-front-file-domain}")
    private String cloudFrontFileDomain;

    private String teamBasicLogoImageFolder;
    private String profileImageFolder;
    private String profileActivityCertificationFileFolder;
    private String profileAwardsCertificationFileFolder;
    private String profileLicenseCertificationFileFolder;
    private String profileLogBodyImageFolder;
    private String profilePortfolioRepresentImageFolder;
    private String profilePortfolioSubImageFolder;

    private String teamProductRepresentImageFolder;
    private String teamProductSubImageFolder;

    private String teamLogBodyImageFolder;

    private String profileEducationCertificationFileFolder;

    public void deleteS3Image(final String imageUrl) {
        try {
            URL url = new URL(imageUrl);
            String path = url.getPath().substring(1);
            s3Client.deleteObject(new DeleteObjectRequest(bucket, path));
        } catch (AmazonServiceException e) {
            throw InvalidImagePathException.EXCEPTION;
        } catch (Exception e) {
            throw new RuntimeException("Error processing URL", e);
        }
    }

    public void deleteFile(final String fileUrl) {
        try {
            URL url = new URL(fileUrl);
            String path = url.getPath().substring(1);
            s3Client.deleteObject(new DeleteObjectRequest(bucket, path));
        } catch (AmazonServiceException e) {
            throw InvalidFilePathException.EXCEPTION;
        } catch (Exception e) {
            throw new RuntimeException("Error processing URL", e);
        }
    }

    public String uploadTeamBasicLogoImage(final ImageFile imageFile) {
        return getImagePath(imageFile, teamBasicLogoImageFolder);
    }

    public String uploadProfileImage(final ImageFile imageFile) {
        return getImagePath(imageFile, profileImageFolder);
    }

    public String uploadProfileLogBodyImage(final ImageFile imageFile) {
        return getImagePath(imageFile, profileLogBodyImageFolder);
    }

    public String uploadProfileProjectRepresentImage(final ImageFile imageFile) {
        return getImagePath(imageFile, profilePortfolioRepresentImageFolder);
    }

    public String uploadProjectSubImage(final ImageFile imageFile) {
        return getImagePath(imageFile, profilePortfolioSubImageFolder);
    }

    public String uploadTeamProductRepresentImage(final ImageFile imageFile) {
        return getImagePath(imageFile, profilePortfolioRepresentImageFolder);
    }

    public String uploadTeamProductSubImage(final ImageFile imageFile) {
        return getImagePath(imageFile, teamProductSubImageFolder);
    }

    public String uploadTeamLogBodyImage(final ImageFile imageFile) {
        return getImagePath(imageFile, teamLogBodyImageFolder);
    }

    @NotNull
    private String getImagePath(ImageFile imageFile, String imageFolderName) {
        final String uploadedImagePath = imageFolderName + imageFile.getHashedName();

        final ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentType(imageFile.getContentType());
        metadata.setContentLength(imageFile.getSize());
        metadata.setCacheControl(CACHE_CONTROL_VALUE);

        try (final InputStream inputStream = imageFile.getInputStream()) {
            s3Client.putObject(bucket, uploadedImagePath, inputStream, metadata);
            String objectUrl = "https://" + cloudFrontImageDomain + "/" + uploadedImagePath;
            return objectUrl;
        } catch (final AmazonServiceException e) {
            throw InvalidImagePathException.EXCEPTION;
        } catch (final IOException e) {
            throw InvalidImageException.EXCEPTION;
        }
    }

    public String uploadProfileActivityFile(final CertificationFile certificationFile) {
        return getFilePath(certificationFile, profileActivityCertificationFileFolder);
    }

    public String uploadProfileAwardsFile(final CertificationFile certificationFile) {
        return getFilePath(certificationFile, profileAwardsCertificationFileFolder);
    }

    public String uploadProfileLicenseFile(final CertificationFile certificationFile) {
        return getFilePath(certificationFile, profileLicenseCertificationFileFolder);
    }

    public String uploadProfileEducationFile(final CertificationFile certificationFile) {
        return getFilePath(certificationFile, profileEducationCertificationFileFolder);
    }

    @NotNull
    private String getFilePath(CertificationFile certificationFile, String fileFolderName) {
        final String uploadedFilePath = fileFolderName + certificationFile.getHashedName();

        final ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentType(certificationFile.getContentType());
        metadata.setContentLength(certificationFile.getSize());
        metadata.setCacheControl(CACHE_CONTROL_VALUE);

        try (final InputStream inputStream = certificationFile.getInputStream()) {
            s3Client.putObject(bucket, uploadedFilePath, inputStream, metadata);
            String objectUrl = "https://" + cloudFrontFileDomain + "/" + uploadedFilePath;
            return objectUrl;
        } catch (final AmazonServiceException e) {
            throw InvalidFilePathException.EXCEPTION;
        } catch (final IOException e) {
            throw InvalidFileException.EXCEPTION;
        }
    }
}
