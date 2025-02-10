package liaison.linkit.mail.service;

import jakarta.mail.MessagingException;
import java.io.UnsupportedEncodingException;
import org.springframework.scheduling.annotation.Async;

public interface TeamMemberInvitationMailService {

    @Async
    void sendMailTeamMemberInvitation(final String teamMemberInvitationEmail, final String teamLogoImagePath, final String teamName, final String teamCode)
        throws MessagingException, UnsupportedEncodingException;
}
