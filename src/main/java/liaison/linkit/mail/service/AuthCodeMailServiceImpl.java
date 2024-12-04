package liaison.linkit.mail.service;

import jakarta.mail.Message;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class AuthCodeMailServiceImpl implements AuthCodeMailService {
    private final JavaMailSender javaMailSender;

    @Value("${google.id}")
    private String mailId;

    @Override
    public void sendMailReAuthenticationCode(final String receiverMailAddress, final String authcode) throws MessagingException {
        final MimeMessage mimeMessage = createReAuthenticationCodeMail(receiverMailAddress, authcode);

        try {
            javaMailSender.send(mimeMessage);
        } catch (Exception e) {
            e.printStackTrace();
            throw new IllegalArgumentException();
        }
    }

    private MimeMessage createReAuthenticationCodeMail(final String receiverMailAddress, final String authCode) throws MessagingException {
        final MimeMessage mimeMessage = javaMailSender.createMimeMessage();

        mimeMessage.addRecipients(Message.RecipientType.TO, receiverMailAddress);
        mimeMessage.setSubject("[링킷] 이메일 변경 인증 코드 발송");

        final String msgg = String.format("""
                           <table align="center" width="100%%" cellspacing="0" cellpadding="0" border="0" style="border-collapse: collapse;">
                                               <tbody>
                                                   <tr>
                                                       <td>
                                                           <table align="center" width="100%%" cellspacing="0" cellpadding="0" border="0" bgcolor="#ffffff" style="max-width: 600px; border-radius: 8px; margin: 0 auto;">
                                                               <tbody>
                                                                   <tr>
                                                                       <td align="left" style="padding: 20px;">
                                                                           <img src="https://image-prod.linkit.im/mail/linkit_color_logo.png" alt="Logo" style="display: block; width: 92px; height: auto;">
                                                                       </td>
                                                                   </tr>
                                                                   <tr>
                                                                       <td align="center" style="padding: 30px 20px;">
                                                                           <span style="font-size: 24px; font-weight: bold;">이메일 인증 코드</span>
                                                                           <p style="font-size: 16px; color: #555; text-align: center;">귀하의 이메일 변경을 위한 인증 코드입니다:</p>
                                                                           <div class="code">%s</div>
                                                                           <p style="font-size: 14px; color: #555; text-align: center;">위 코드를 인증 폼에 입력해 주세요.</p>
                                                                       </td>
                                                                   </tr>
                                                               </tbody>
                                                           </table>
                                                       </td>
                                                   </tr>
                                               </tbody>
                                           </table>     
                """, authCode);

        mimeMessage.setContent(msgg, "text/html; charset=utf-8");
        mimeMessage.setFrom(id);

        return mimeMessage;
    }
}
