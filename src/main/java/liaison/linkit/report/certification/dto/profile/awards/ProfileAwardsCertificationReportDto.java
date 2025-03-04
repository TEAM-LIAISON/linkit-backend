package liaison.linkit.report.certification.dto.profile.awards;

import java.time.LocalDateTime;

import lombok.Builder;

@Builder
public record ProfileAwardsCertificationReportDto(
        Long profileAwardsId,
        String emailId,
        String awardsName,
        String awardsCertificationAttachFileName,
        String awardsCertificationAttachFilePath,
        LocalDateTime uploadTime) {}
