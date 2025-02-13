package liaison.linkit.report.certification.dto.license;

import java.time.LocalDateTime;
import lombok.Builder;

public record ProfileLicenseCertificationReportDto(
    Long profileLicenseId,
    String emailId,
    String licenseName,
    LocalDateTime uploadTime
) {

    @Builder
    public ProfileLicenseCertificationReportDto(
        Long profileLicenseId,
        String emailId,
        String licenseName
    ) {
        this(
            profileLicenseId,
            emailId,
            licenseName,
            LocalDateTime.now()
        );
    }
}
