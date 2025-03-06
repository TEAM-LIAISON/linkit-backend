package liaison.linkit.report.certification.dto.profile.education;

import java.time.LocalDateTime;

import lombok.Builder;

@Builder
public record ProfileEducationCertificationReportDto(
        Long profileEducationId,
        String emailId,
        String universityName,
        String educationCertificationAttachFileName,
        String educationCertificationAttachFilePath,
        LocalDateTime uploadTime) {}
