package liaison.linkit.report.certification.dto.profile.activity;

import java.time.LocalDateTime;
import lombok.Builder;

public record ProfileActivityCertificationReportDto(
    Long profileActivityId,
    String emailId,
    String activityName,
    LocalDateTime uploadTime
) {

    @Builder
    public ProfileActivityCertificationReportDto(
        Long profileActivityId,
        String emailId,
        String activityName
    ) {
        this(
            profileActivityId,
            emailId,
            activityName,
            LocalDateTime.now()
        );
    }
}
