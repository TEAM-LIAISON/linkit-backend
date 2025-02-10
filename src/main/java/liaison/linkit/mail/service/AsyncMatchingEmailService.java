package liaison.linkit.mail.service;

import jakarta.mail.MessagingException;
import java.io.UnsupportedEncodingException;
import org.springframework.scheduling.annotation.Async;

public interface AsyncMatchingEmailService {

    @Async
    void sendMatchingCompletedEmails(
        final String senderMailTitle,
        final String senderMailSubTitle,
        final String senderMailSubText,

        final String receiverMailTitle,
        final String receiverMailSubTitle,
        final String receiverMailSubText,

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

        final String matchingReceiverRegionOrAnnouncementSkillText,
        final String matchingReceiverRegionOrAnnouncementSkill
    ) throws MessagingException, UnsupportedEncodingException;

    @Async
    void sendMatchingRequestedEmail(
        final String receiverMailTitle,
        final String receiverMailSubTitle,
        final String receiverMailSubText,

        final String matchingReceiverEmail,
        final String matchingSenderName,
        final String matchingSenderLogoImagePath,
        final String positionOrTeamSize,
        final String matchingSenderPositionOrTeamSize,
        final String matchingSenderRegionDetail
    ) throws MessagingException, UnsupportedEncodingException;
}
