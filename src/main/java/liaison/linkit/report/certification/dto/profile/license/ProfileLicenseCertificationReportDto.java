package liaison.linkit.report.certification.dto.profile.license;

import java.time.LocalDateTime;

import lombok.Builder;

@Builder
public record ProfileLicenseCertificationReportDto(
        Long profileLicenseId,
        String emailId,
        String licenseName,
        String licenseCertificationAttachFileName,
        String licenseCertificationAttachFilePath,
        LocalDateTime uploadTime) {}
