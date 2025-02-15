package liaison.linkit.report.certification.dto.profile.education;

import java.time.LocalDateTime;
import lombok.Builder;

public record ProfileEducationCertificationReportDto(
    Long profileEducationId,
    String emailId,
    String universityName,
    String educationCertificationAttachFileName,
    String educationCertificationAttachFilePath,
    LocalDateTime uploadTime
) {

    @Builder
    public ProfileEducationCertificationReportDto(
        Long profileEducationId,
        String emailId,
        String universityName,
        String educationCertificationAttachFileName,
        String educationCertificationAttachFilePath
    ) {
        this(
            profileEducationId,
            emailId,
            universityName,
            educationCertificationAttachFileName,
            educationCertificationAttachFilePath,
            LocalDateTime.now()
        );
    }
}
