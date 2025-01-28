package liaison.linkit.mail.service;

import jakarta.mail.MessagingException;
import org.springframework.scheduling.annotation.Async;

public interface AsyncAuthCodeMailService {

    // 이메일 재인증 코드 발송
    @Async
    void sendMailReAuthenticationCode(final String receiverName, final String receiverMailAddress, final String authCode) throws MessagingException;

}
