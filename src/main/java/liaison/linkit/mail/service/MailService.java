package liaison.linkit.mail.service;

import jakarta.mail.MessagingException;

import java.time.LocalDateTime;

public interface MailService {

    // 1. 내 이력서 -> 내 이력서 매칭 요청 보낸 경우
    void mailPrivateToPrivate(
            final String receiverEmail,
            final String receiverName,
            final String senderName,
            final LocalDateTime requestDate,
            final String requestMessage
    ) throws Exception;

    /*
        1. 수신자 이름 - receiverName
        2. 발신자 이름 - senderName
        3. 매칭 요청 발생 날짜 - requestDate
        4. 매칭 요청 보낼 때 메시지 - requestMessage
     */

    // 2. 팀 소개서 -> 내 이력서 매칭 요청 보낸 경우
    void mailTeamToPrivate(
            final String receiverEmail,
            final String receiverName,
            final String senderName,
            final LocalDateTime requestDate,
            final String requestMessage
    ) throws Exception;

    // 3. 내 이력서 -> 팀 소개서로 매칭 요청 보낸 경우
    void mailPrivateToTeam(
            final String receiverEmail,
            final String receiverName,
            final String senderName,
            final LocalDateTime requestDate,
            final String requestMessage
    ) throws Exception;

    // 4. 팀 소개서 -> 팀 소개서로 매칭 요청 보낸 경우
    void mailTeamToTeam(
            final String receiverEmail,
            final String receiverName,
            final String senderName,
            final LocalDateTime requestDate,
            final String requestMessage
    ) throws Exception;

    // 5. 내 이력서 -> 내 이력서 매칭 성사 (발신자 메일)
    void mailSuccessPrivateToPrivateSender(
            final String senderEmail,
        final String receiverName,
        final String receiverEmail,
        final String requestMessage
    ) throws MessagingException;

    // 6. 내 이력서 -> 내 이력서 매칭 성사 (수신자 메일)
    void mailSuccessPrivateToPrivateReceiver(
            final String senderName, final String senderEmail, final String receiverEmail, final String requestMessage
    ) throws MessagingException;

    // 7. 팀 소개서 -> 내 이력서 매칭 성사 (발신자 메일)
    void mailSuccessTeamToPrivateSender(
            final String senderEmail,
            final String receiverName,
            final String receiverEmail,
            final String requestMessage
    ) throws MessagingException;

    // 8. 팀 소개서 -> 내 이력서 매칭 성사 (수신자 메일)
    void mailSuccessTeamToPrivateReceiver(
            final String senderName, final String senderEmail, final String receiverEmail, final String requestMessage
    ) throws MessagingException;

    // 9. 내 이력서 -> 팀 소개서 매칭 성사 (발신자 메일)
    void mailSuccessPrivateToTeamSender(
            final String senderEmail,
            final String receiverName,
            final String receiverEmail,
            final String requestMessage
    ) throws MessagingException;

    // 10. 내 이력서 -> 팀 소개서 매칭 성사 (수신자 메일)
    void mailSuccessPrivateToTeamReceiver(
            final String senderName, final String senderEmail, final String receiverEmail, final String requestMessage
    ) throws MessagingException;

    // 11. 팀 소개서 -> 팀 소개서 매칭 성사 (발신자 메일)
    void mailSuccessTeamToTeamSender(
            final String senderEmail,
            final String receiverName,
            final String receiverEmail,
            final String requestMessage
    ) throws MessagingException;

    // 12. 팀 소개서 -> 팀 소개서 매칭 성사 (수신자 메일)
    void mailSuccessTeamToTeamReceiver(
            final String senderName, final String senderEmail, final String receiverEmail, final String requestMessage
    ) throws MessagingException;
}
