package liaison.linkit.mail.service;

import java.io.UnsupportedEncodingException;

import jakarta.mail.MessagingException;

import org.springframework.scheduling.annotation.Async;

public interface TeamMemberInvitationMailService {

    @Async
    void sendMailTeamMemberInvitation(
            final String teamMemberInvitationEmail,
            final String teamLogoImagePath,
            final String teamName,
            final String teamCode)
            throws MessagingException, UnsupportedEncodingException;
}
