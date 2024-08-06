package liaison.linkit.mail.service;

import java.time.LocalDateTime;

public interface MailService {

    // 1. 내 이력서 -> 내 이력서 매칭 요청 보낸 경우
    void mailPrivateToPrivate(
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






//    // 2. 내 이력서 -> 팀 소개서 매칭 요청 보낸 경우
//    void mailPrivateToTeam() throws Exception;
//    // 3. 팀 소개서 -> 내 이력서 매칭 요청 보낸 경우
//    void mailTeamToPrivate() throws Exception;
//    // 4. 팀 소개서 -> 팀 소개서 매칭 요청 보낸 경우
//    void mailTeamToTeam() throws Exception;
//
//    // 5. 내 이력서 관련 - 매칭 성사된 경우
//    void mailSuccessPrivate() throws Exception;
//    // 6. 팀 소개서 관련 - 매칭 성사된 경우
//    void mailSuccessTeam() throws Exception;
}
