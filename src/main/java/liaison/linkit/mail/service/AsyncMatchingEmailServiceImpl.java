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
public class AsyncMatchingEmailServiceImpl implements AsyncMatchingEmailService {
    private final JavaMailSender javaMailSender;

    @Value("${google.id}")
    private String mailId;

    @Override
    public void sendMatchingCompletedEmails(
            final String matchingSenderEmail,
            final String matchingSenderName,
            final String matchingSenderLogoImagePath,
            final String matchingSenderPositionOrTeamSize,
            final String matchingSenderRegionDetail,
            final String matchingReceiverEmail,
            final String matchingReceiverName,
            final String matchingReceiverLogoImagePath,
            final String matchingReceiverPositionOrTeamSize,
            final String matchingReceiverRegionDetail
    ) throws MessagingException {
        final MimeMessage matchingSenderMail = createMatchingCompletedMail(
                matchingSenderEmail,
                matchingSenderName,
                matchingSenderLogoImagePath,
                matchingSenderPositionOrTeamSize,
                matchingSenderRegionDetail
        );

        final MimeMessage matchingReceiverMail = createMatchingCompletedMail(
                matchingReceiverEmail,
                matchingReceiverName,
                matchingReceiverLogoImagePath,
                matchingReceiverPositionOrTeamSize,
                matchingReceiverRegionDetail
        );

        try {
            javaMailSender.send(matchingSenderMail);
            javaMailSender.send(matchingReceiverMail);
        } catch (Exception e) {
            log.error("Failed to send matching completed email", e);
            throw new IllegalArgumentException("Failed to send email");
        }
    }

    private MimeMessage createMatchingCompletedMail(
            final String receiverEmail,
            final String otherPartyName,
            final String otherPartyLogoImagePath,
            final String otherPartyPositionOrTeamSize,
            final String otherPartyRegionDetail
    ) throws MessagingException {
        final MimeMessage mimeMessage = javaMailSender.createMimeMessage();

        mimeMessage.addRecipients(Message.RecipientType.TO, receiverEmail);
        mimeMessage.setSubject("[링킷] 매칭 성사 알림입니다");

        final String msgg = String.format("""
                <table border="0" cellpadding="0" cellspacing="0" width="100%%" style="border-collapse:collapse; background-color: #ffffff;">
                  <tbody>
                    <tr>
                      <td>
                        <table align="center" width="100%%" cellspacing="0" cellpadding="0" border="0" bgcolor="#F1F4F9" style="max-width: 642px; margin: 0 auto;">
                          <tbody>
                            <tr>
                              <td align="left" style="padding: 20px; background-color: #FFFFFF;">
                                <img src="https://image-prod.linkit.im/mail/linkit_color_logo.png" alt="Logo" style="display: block; width: 92px; height: auto;">
                              </td>
                            </tr>
                            <tr>
                              <td style="background-color: #CBD4E1; height: 1px;"></td>
                            </tr>
                            <tr>
                              <td style="padding: 30px 20px; text-align: center;">
                                <h1 style="font-family: Pretendard, 'Apple SD Gothic Neo', 'Noto Sans KR', -apple-system, BlinkMacSystemFont, system-ui, sans-serif; font-size: 20px; font-weight: 600; margin: 0 0 16px 0; color: #2563EB; word-break: keep-all;">'%s'님과 매칭 성사!</h1>
                                <p style="font-family: Pretendard, 'Apple SD Gothic Neo', 'Noto Sans KR', -apple-system, BlinkMacSystemFont, system-ui, sans-serif; font-size: 12px; color: #64748B; line-height: 1.4; margin: 0; word-break: keep-all;">
                                  매칭이 성사되었어요.<br/>
                                  채팅을 통해 이야기를 나눠 보세요!
                                </p>
                              </td>
                            </tr>
                            <tr>
                              <td style="padding: 0 20px;">
                                <table border="0" cellpadding="0" cellspacing="0" width="100%%" bgcolor="#EDF3FF" style="border: 1px solid #CBD4E1; border-radius: 8px;">
                                  <tr>
                                    <td style="padding: 24px 24px;">
                                      <table border="0" cellpadding="0" cellspacing="0" width="100%%">
                                        <tr>
                                          <td width="76px" style="vertical-align: middle;">
                                            <img src="%s" alt="프로필 이미지" style="width: 76px; height: 76px; border-radius: 15px;">
                                          </td>
                                          <td width="16px"></td>
                                          <td style="vertical-align: middle;">
                                            <p style="font-family: Pretendard, 'Apple SD Gothic Neo', 'Noto Sans KR', -apple-system, BlinkMacSystemFont, system-ui, sans-serif; font-size: 18px; font-weight: 600; margin: 0 0 8px 0;">%s</p>
                                            <table cellpadding="0" cellspacing="0" border="0" style="font-family: Pretendard, 'Apple SD Gothic Neo', 'Noto Sans KR', -apple-system, BlinkMacSystemFont, system-ui, sans-serif; font-size: 12px; color: #94A3B8;">
                                              <tr>
                                                <td style="padding-bottom: 4px; width: 36px;">포지션</td>
                                                <td style="padding-bottom: 4px; padding-left: 8px;">| %s</td>
                                              </tr>
                                              <tr>
                                                <td style="width: 36px;">지역</td>
                                                <td style="padding-left: 8px;">| %s</td>
                                              </tr>
                                            </table>
                                          </td>
                                        </tr>
                                      </table>
                                    </td>
                                  </tr>
                                </table>
                              </td>
                            </tr>
                            <tr>
                              <td style="padding: 30px 20px; text-align: center;">
                                <a href="https://www.linkit.im" style="display: inline-block; padding: 12px 24px; background-color: #2563EB; color: #FFFFFF; text-decoration: none; border-radius: 24px;">
                                  <img src="https://image-prod.linkit.im/mail/chat_icon_image.png" alt="채팅" style="width: 20px; height: 20px; vertical-align: middle; margin-right: 8px;">
                                  채팅하기
                                </a>
                              </td>
                            </tr>
                            <tr>
                              <td style="padding: 0 20px 30px;">
                                <p style="font-family: Pretendard, 'Apple SD Gothic Neo', 'Noto Sans KR', -apple-system, BlinkMacSystemFont, system-ui, sans-serif; font-size: 12px; color: #94A3B8; margin: 0; word-break: keep-all;">*본인이 아닌 경우 이메일을 삭제해 주세요</p>
                              </td>
                            </tr>
                            <tr>
                              <td style="background-color: #CBD4E1; height: 1px;"></td>
                            </tr>
                            <tr>
                              <td style="padding: 20px; background-color: #FFFFFF;">
                                <img src="https://image-prod.linkit.im/mail/linkit_grey_logo.png" alt="Logo" style="display: block; width: 92px; height: auto; margin-bottom: 20px;">
                                <p style="font-family: Pretendard, 'Apple SD Gothic Neo', 'Noto Sans KR', -apple-system, BlinkMacSystemFont, system-ui, sans-serif; font-size: 11px; color: #94A3B8; line-height: 2.0; margin: 0; text-align: left; word-break: keep-all;">
                                  리에종 ㅣ 대표 : 주서영 ㅣ 개인정보관리책임자 : 권동민<br/>
                                  주소 : 서울특별시 종로구 127 ㅣ메일 : linkit@linkit.im<br/>
                                  Copyright ⓒ 2024. liaison All rights reserved.<br/>
                                  ※ 본 메일은 매칭 성사 알림을 위해 발송되었습니다
                                </p>
                              </td>
                            </tr>
                            <tr>
                              <td style="background-color: #CBD4E1; height: 1px;"></td>
                            </tr>
                          </tbody>
                        </table>
                      </td>
                    </tr>
                  </tbody>
                </table>
                """, otherPartyName, otherPartyLogoImagePath, otherPartyName, otherPartyPositionOrTeamSize, otherPartyRegionDetail);

        mimeMessage.setContent(msgg, "text/html; charset=utf-8");
        mimeMessage.setFrom(mailId);

        return mimeMessage;
    }
}
