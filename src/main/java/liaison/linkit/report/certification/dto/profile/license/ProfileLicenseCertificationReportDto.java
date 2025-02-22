package liaison.linkit.report.certification.dto.profile.license;

import java.time.LocalDateTime;

import lombok.Builder;

public record ProfileLicenseCertificationReportDto(
        Long profileLicenseId,
        String emailId,
        String licenseName,
        String licenseCertificationAttachFileName,
        String licenseCertificationAttachFilePath,
        LocalDateTime uploadTime) {

    @Builder
    public ProfileLicenseCertificationReportDto(
            Long profileLicenseId,
            String emailId,
            String licenseName,
            String licenseCertificationAttachFileName,
            String licenseCertificationAttachFilePath) {
        this(
                profileLicenseId,
                emailId,
                licenseName,
                licenseCertificationAttachFileName,
                licenseCertificationAttachFilePath,
                LocalDateTime.now());
    }
}
