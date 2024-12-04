package liaison.linkit.mail.service;

import jakarta.mail.MessagingException;

public interface AuthCodeMailService {

    // 이메일 재인증 코드 발송
    void sendMailReAuthenticationCode(final String receiverMailAddress, final String authCode) throws MessagingException;

}
