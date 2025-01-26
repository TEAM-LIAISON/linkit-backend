package liaison.linkit.mail.service;

import jakarta.mail.MessagingException;
import org.springframework.scheduling.annotation.Async;

public interface AsyncMatchingEmailService {

    @Async
    void sendMatchingCompletedEmails(
            final String matchingSenderEmail,
            final String matchingSenderName,
            final String matchingSenderLogoImagePath,
            final String matchingSenderPositionOrTeamSize,
            final String matchingSenderRegionDetail,
            final String matchingReceiverEmail,
            final String matchingReceiverName,
            final String matchingReceiverLogoImagePath,
            final String matchingReceiverPositionOrTeamSize,
            final String matchingReceiverRegionDetail
    ) throws MessagingException;
}
