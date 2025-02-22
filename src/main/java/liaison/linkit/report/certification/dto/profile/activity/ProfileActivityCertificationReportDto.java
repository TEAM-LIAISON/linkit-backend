package liaison.linkit.report.certification.dto.profile.activity;

import java.time.LocalDateTime;

import lombok.Builder;

public record ProfileActivityCertificationReportDto(
        Long profileActivityId,
        String emailId,
        String activityName,
        String activityCertificationAttachFileName,
        String activityCertificationAttachFilePath,
        LocalDateTime uploadTime) {

    @Builder
    public ProfileActivityCertificationReportDto(
            Long profileActivityId,
            String emailId,
            String activityName,
            String activityCertificationAttachFileName,
            String activityCertificationAttachFilePath) {
        this(
                profileActivityId,
                emailId,
                activityName,
                activityCertificationAttachFileName,
                activityCertificationAttachFilePath,
                LocalDateTime.now());
    }
}
