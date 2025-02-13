package liaison.linkit.report.certification.dto.awards;

import java.time.LocalDateTime;
import lombok.Builder;

public record ProfileAwardsCertificationReportDto(
    Long profileAwardsId,
    String emailId,
    String awardsName,
    LocalDateTime uploadTime
) {

    @Builder
    public ProfileAwardsCertificationReportDto(
        Long profileAwardsId,
        String emailId,
        String awardsName
    ) {
        this(
            profileAwardsId,
            emailId,
            awardsName,
            LocalDateTime.now()
        );
    }
}
