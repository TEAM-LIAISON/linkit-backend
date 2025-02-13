package liaison.linkit.report.certification.dto.education;

import java.time.LocalDateTime;
import lombok.Builder;

public record ProfileEducationCertificationReportDto(
    Long profileEducationId,
    String emailId,
    String universityName,
    LocalDateTime uploadTime
) {

    @Builder
    public ProfileEducationCertificationReportDto(
        Long profileEducationId,
        String emailId,
        String universityName
    ) {
        this(
            profileEducationId,
            emailId,
            universityName,
            LocalDateTime.now()
        );
    }
}
