package liaison.linkit.file.infrastructure;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.DeleteObjectRequest;
import com.amazonaws.services.s3.model.ObjectMetadata;
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

    @Value("${cloud.aws.s3.cloud-front-image-domain}")
    private String cloudFrontImageDomain;

    private static final String IMAGE_PROFILE_MAIN_FOLDER = "image/profile/main/";
    private static final String IMAGE_PROFILE_LOG_FOLDER = "image/profile/log/";

    private static final String IMAGE_PROFILE_PORTFOLIO_MAIN_FOLDER =
            "image/profile/portfolio/main/";
    private static final String IMAGE_PROFILE_PORTFOLIO_SUB_FOLDER = "image/profile/portfolio/sub/";

    private static final String FILE_PROFILE_ACTIVITY_FOLDER = "image/profile/activity/";
    private static final String FILE_PROFILE_EDUCATION_FOLDER = "file/profile/education/";
    private static final String FILE_PROFILE_AWARDS_FOLDER = "file/profile/awards/";
    private static final String FILE_PROFILE_LICENSE_FOLDER = "file/profile/license/";

    private static final String IMAGE_TEAM_MAIN_FOLDER = "image/team/main/";
    private static final String IMAGE_TEAM_LOG_FOLDER = "image/team/log/";
    private static final String IMAGE_TEAM_PRODUCT_MAIN_FOLDER = "image/team/product/main/";
    private static final String IMAGE_TEAM_PRODUCT_SUB_FOLDER = "image/team/product/sub/";

    /** 이미지 삭제 */
    public void deleteS3Image(final String imageUrl) {
        try {
            URL url = new URL(imageUrl);
            String path = url.getPath().substring(1); // 맨 앞 "/" 제거
            s3Client.deleteObject(new DeleteObjectRequest(bucket, path));
        } catch (AmazonServiceException e) {
            log.error("잘못된 이미지 경로 : {}", imageUrl, e);
            throw InvalidImagePathException.EXCEPTION;
        } catch (Exception e) {
            log.error("URL 처리 에러 : {}", imageUrl, e);
            throw new RuntimeException("URL 처리 에러", e);
        }
    }

    /** 증명서 삭제 */
    public void deleteS3File(final String fileUrl) {
        try {
            URL url = new URL(fileUrl);
            String path = url.getPath().substring(1);
            s3Client.deleteObject(new DeleteObjectRequest(bucket, path));
        } catch (AmazonServiceException e) {
            log.error("잘못된 인증서 경로 : {}", fileUrl, e);
            throw InvalidFilePathException.EXCEPTION;
        } catch (Exception e) {
            log.error("URL 처리 에러 : {}", fileUrl, e);
            throw new RuntimeException("URL 처리 에러", e);
        }
    }

    /** 프로필 일반 이미지 업로드 */
    public String uploadProfileMainImage(final ImageFile imageFile) {
        return uploadImage(imageFile, IMAGE_PROFILE_MAIN_FOLDER);
    }

    /** 프로필 로그 이미지 업로드 */
    public String uploadProfileLogBodyImage(final ImageFile imageFile) {
        return uploadImage(imageFile, IMAGE_PROFILE_LOG_FOLDER);
    }

    /** 프로필 프로젝트 대표 이미지 업로드 */
    public String uploadProfileProjectRepresentImage(final ImageFile imageFile) {
        return uploadImage(imageFile, IMAGE_PROFILE_PORTFOLIO_MAIN_FOLDER);
    }

    /** 프로필 프로젝트 서브 이미지 업로드 */
    public String uploadProfileProjectSubImage(final ImageFile imageFile) {
        return uploadImage(imageFile, IMAGE_PROFILE_PORTFOLIO_SUB_FOLDER);
    }

    /** 팀 로고 이미지 업로드 */
    public String uploadTeamLogoImage(final ImageFile imageFile) {
        return uploadImage(imageFile, IMAGE_TEAM_MAIN_FOLDER);
    }

    /** 팀 로그 이미지 업로드 */
    public String uploadTeamLogBodyImage(final ImageFile imageFile) {
        return uploadImage(imageFile, IMAGE_TEAM_LOG_FOLDER);
    }

    /** 팀 프로덕트 대표 이미지 업로드 */
    public String uploadTeamProductRepresentImage(final ImageFile imageFile) {
        return uploadImage(imageFile, IMAGE_TEAM_PRODUCT_MAIN_FOLDER);
    }

    /** 팀 프로덕트 서브 이미지 업로드 */
    public String uploadTeamProductSubImage(final ImageFile imageFile) {
        return uploadImage(imageFile, IMAGE_TEAM_PRODUCT_SUB_FOLDER);
    }

    /** 프로필 이력 증명 파일 업로드 [01] */
    public String uploadProfileActivityFile(final CertificationFile file) {
        return uploadFile(file, FILE_PROFILE_ACTIVITY_FOLDER);
    }

    /** 프로필 학력 증명 파일 업로드 [02] */
    public String uploadProfileEducationFile(final CertificationFile file) {
        return uploadFile(file, FILE_PROFILE_EDUCATION_FOLDER);
    }

    /** 프로필 수상 증명 파일 업로드 [03] */
    public String uploadProfileAwardsFile(final CertificationFile file) {
        return uploadFile(file, FILE_PROFILE_AWARDS_FOLDER);
    }

    /** 프로필 자격증 증명 파일 업로드 [04] */
    public String uploadProfileLicenseFile(final CertificationFile file) {
        return uploadFile(file, FILE_PROFILE_LICENSE_FOLDER);
    }

    /** 이미지 업로드 공통 로직 */
    private String uploadImage(final ImageFile imageFile, final String folderPath) {
        final String s3Key = folderPath + imageFile.getHashedName();
        final ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentType(imageFile.getContentType());
        metadata.setContentLength(imageFile.getSize());
        metadata.setCacheControl(CACHE_CONTROL_VALUE);

        try (final InputStream inputStream = imageFile.getInputStream()) {
            s3Client.putObject(bucket, s3Key, inputStream, metadata);
            return "https://" + cloudFrontImageDomain + "/" + s3Key;
        } catch (AmazonServiceException e) {
            log.error("이미지 업로드 실패 : {}", s3Key, e);
            throw InvalidImagePathException.EXCEPTION;
        } catch (IOException e) {
            log.error("이미지 업로드 IO 에러 : {}", s3Key, e);
            throw InvalidImageException.EXCEPTION;
        }
    }

    /** 파일 업로드 공통 로직 */
    private String uploadFile(final CertificationFile file, final String folderPath) {
        final String s3Key = folderPath + file.getHashedName();
        final ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentType(file.getContentType());
        metadata.setContentLength(file.getSize());
        metadata.setCacheControl(CACHE_CONTROL_VALUE);

        try (final InputStream inputStream = file.getInputStream()) {
            s3Client.putObject(bucket, s3Key, inputStream, metadata);
            return "https://" + cloudFrontImageDomain + "/" + s3Key;
        } catch (AmazonServiceException e) {
            log.error("파일 업로드 실패 : {}", s3Key, e);
            throw InvalidFilePathException.EXCEPTION;
        } catch (IOException e) {
            log.error("파일 업로드 IO 에러 : {}", s3Key, e);
            throw InvalidFileException.EXCEPTION;
        }
    }
}
