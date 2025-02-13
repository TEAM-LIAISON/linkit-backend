package liaison.linkit.report.certification.dto;

import java.time.LocalDateTime;
import lombok.Builder;

public record ProfileLicenseCertificationReportDto(
    Long profileLicenseId,
    String memberName,
    String licenseName,
    LocalDateTime uploadTime
) {

    @Builder
    public ProfileLicenseCertificationReportDto(
        Long profileLicenseId,
        String memberName,
        String licenseName
    ) {
        this(
            profileLicenseId,
            memberName,
            licenseName,
            LocalDateTime.now()
        );
    }
}
