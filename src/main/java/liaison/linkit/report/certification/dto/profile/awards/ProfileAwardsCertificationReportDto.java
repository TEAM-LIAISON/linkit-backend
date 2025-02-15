package liaison.linkit.report.certification.dto.profile.awards;

import java.time.LocalDateTime;
import lombok.Builder;

public record ProfileAwardsCertificationReportDto(
    Long profileAwardsId,
    String emailId,
    String awardsName,
    String awardsCertificationAttachFileName,
    String awardsCertificationAttachFilePath,
    LocalDateTime uploadTime
) {

    @Builder
    public ProfileAwardsCertificationReportDto(
        Long profileAwardsId,
        String emailId,
        String awardsName,
        String awardsCertificationAttachFileName,
        String awardsCertificationAttachFilePath
    ) {
        this(
            profileAwardsId,
            emailId,
            awardsName,
            awardsCertificationAttachFileName,
            awardsCertificationAttachFilePath,
            LocalDateTime.now()
        );
    }
}
