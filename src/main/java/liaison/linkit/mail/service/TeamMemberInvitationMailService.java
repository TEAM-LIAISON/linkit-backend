package liaison.linkit.mail.service;

import jakarta.mail.MessagingException;

public interface TeamMemberInvitationMailService {
    void sendMailTeamMemberInvitation(final String teamMemberInvitationEmail, final String teamLogoImagePath, final String teamName, final String teamCode) throws MessagingException;
}
