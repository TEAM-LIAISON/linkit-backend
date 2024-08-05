package liaison.linkit.mail.service;

public interface MailService {
    // 메일 발송
    void sendSimpleMessage(String to)throws Exception;
}
