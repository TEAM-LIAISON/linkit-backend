//package liaison.linkit.mail.infrastructure;
//
//import jakarta.mail.MessagingException;
//import jakarta.mail.internet.MimeMessage;
//import liaison.linkit.notification.domain.Notification;
//import lombok.RequiredArgsConstructor;
//import org.springframework.mail.javamail.JavaMailSender;
//import org.springframework.mail.javamail.MimeMessageHelper;
//import org.springframework.scheduling.annotation.Async;
//import org.springframework.stereotype.Component;
//
//@Component
//@RequiredArgsConstructor
//public class MailSender {
//
//    private final JavaMailSender javaMailSender;
//
//    private static final String EMAIL_TITLE_PREFIX = "[Linkit] ";
//
//    @Async
//    public void sendNotificationEmail(final Notification notification) throws MessagingException {
//        MimeMessage message = javaMailSender.createMimeMessage();
//        MimeMessageHelper messageHelper = new MimeMessageHelper(message, true, "UTF-8");
//
//        // 메일 제목 설정
////        messageHelper.setSubject(EMAIL_TITLE_PREFIX + notification.getContent());
//
//        // 메일 수신자 설정
////        messageHelper.setTo(notification.getMember().getEmail());
//
//        // html에 들어갈 동적데이터 설정하기
//
//        // 이메일에 포함될 이미지 설정
//
//        // 메일 전송
//        javaMailSender.send(message);
//    }
//}
