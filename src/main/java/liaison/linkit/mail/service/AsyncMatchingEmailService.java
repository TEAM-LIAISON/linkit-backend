package liaison.linkit.mail.service;

import jakarta.mail.MessagingException;
import org.springframework.scheduling.annotation.Async;

public interface AsyncMatchingEmailService {

    @Async
    void sendMatchingCompletedEmails(
            final String matchingSenderEmail,
            final String matchingSenderName,
            final String matchingSenderLogoImagePath,
            final String matchingSenderPositionOrTeamSIzeText,
            final String matchingSenderPositionOrTeamSize,
            final String matchingSenderRegionDetail,

            final String matchingReceiverEmail,
            final String matchingReceiverName,
            final String matchingReceiverLogoImagePath,
            final String matchingReceiverPositionOrTeamSIzeText,
            final String matchingReceiverPositionOrTeamSize,
            final String matchingReceiverRegionDetail
    ) throws MessagingException;

    @Async
    void sendMatchingRequestedEmail(
            final String matchingReceiverEmail,
            final String matchingSenderName,
            final String matchingSenderLogoImagePath,
            final String positionOrTeamSize,
            final String matchingSenderPositionOrTeamSize,
            final String matchingSenderRegionDetail
    ) throws MessagingException;
}
