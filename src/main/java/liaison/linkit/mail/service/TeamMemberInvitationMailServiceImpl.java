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
public class TeamMemberInvitationMailServiceImpl implements TeamMemberInvitationMailService {
    private final JavaMailSender javaMailSender;

    @Value("${google.id}")
    private String mailId;

    @Override
    public void sendMailTeamMemberInvitation(final String teamMemberInvitationEmail, final String teamLogoImagePath, final String teamName) throws MessagingException {
        final MimeMessage mimeMessage = createTeamMemberInvitationMail(teamMemberInvitationEmail, teamLogoImagePath, teamName);

        try {
            javaMailSender.send(mimeMessage);
        } catch (Exception e) {
            e.printStackTrace();
            throw new IllegalArgumentException();
        }
    }

    private MimeMessage createTeamMemberInvitationMail(final String teamMemberInvitationEmail, final String teamLogoImagePath, final String teamName) throws MessagingException {
        final MimeMessage mimeMessage = javaMailSender.createMimeMessage();

        mimeMessage.addRecipients(Message.RecipientType.TO, teamMemberInvitationEmail);
        mimeMessage.setSubject("[링킷] 이메일 변경 인증 코드 발송");

        final String msgg = String.format("""
                    <table border="0" cellpadding="0" cellspacing="0" width="100%%" style="border-collapse:collapse; background-color: #ffffff;"></table>
                        <tbody>
                          <tr>
                            <td>
                            <table align="center" width="100%%" cellspacing="0" cellpadding="0" border="0" bgcolor="#F1F4F9"
                                style="max-width: 642px; margin: 0 auto;">
                                <tbody>
                                    <tr>
                                        <td align="left" style="padding: 1.25rem; background-color: #FFFFFF;">
                                        <img src="https://image-prod.linkit.im/mail/linkit_color_logo.png" alt="Logo" style="display: block; width: 5.75rem; height: auto;">
                                        </td>
                                    </tr>
                                    <tr>
                                        <td style="background-color: #CBD4E1; height: 1px;"></td>
                                    </tr>
                                <tr>
                                    <td style="padding: 1.875rem 1.25rem; text-align: center;">
                                      <h1 style="font-family: Pretendard, 'Apple SD Gothic Neo', 'Noto Sans KR', -apple-system, BlinkMacSystemFont, system-ui, sans-serif; font-size: 1.25rem; font-weight: 600; margin: 0 0 1rem 0; word-break: keep-all;">%s의 초대</h1>
                                      <p style="font-family: Pretendard, 'Apple SD Gothic Neo', 'Noto Sans KR', -apple-system, BlinkMacSystemFont, system-ui, sans-serif; font-size: 0.75rem; color: #64748B; line-height: 1.4; margin: 0; word-break: keep-all;">
                                        '%s'의 팀 구성원으로 초대되었어요.<br/>
                                        링킷에 가입하고 팀 프로필을 확인해 보세요!
                                      </p>
                                    </td>
                                </tr>
                                <tr>
                                    <td style="padding: 0 1.25rem; text-align: center;">
                                        <img src="%s" alt="팀 로고" style="width: 4rem; height: 4rem; margin-bottom: 1rem;">
                                        <p style="font-family: Pretendard, 'Apple SD Gothic Neo', 'Noto Sans KR', -apple-system, BlinkMacSystemFont, system-ui, sans-serif; font-size: 1.25rem; font-weight: 600; margin: 0;">%s</p>
                                    </td>
                                </tr>
                                <tr>
                                    <td style="padding: 1.875rem 1.25rem; text-align: center;">
                                        <a href="%s" style="display: inline-block; padding: 0.75rem 1.5rem; background-color: #2563EB; color: #FFFFFF; text-decoration: none; border-radius: 1.5rem;">팀 프로필 보러가기</a>
                                    </td>
                                </tr>
                                <tr>
                                  <td style="padding: 1.875rem 1.25rem;">
                                    <p style="font-family: Pretendard, 'Apple SD Gothic Neo', 'Noto Sans KR', -apple-system, BlinkMacSystemFont, system-ui, sans-serif; font-size: 0.75rem; color: #94A3B8; margin: 0; word-break: keep-all;">*이메일 변경을 요청하시지 않은 경우 이메일을 삭제해 주세요</p>
                                  </td>
                                </tr>
                                <tr>
                                    <td style="background-color: #CBD4E1; height: 1px;"></td>
                                </tr>
                                <tr>
                                    <td style="padding: 1.25rem; background-color: #FFFFFF;">
                                        <img src="https://image-prod.linkit.im/mail/linkit_grey_logo.png" alt="Logo" style="display: block; width: 5.75rem; height: auto; margin-bottom: 1.25rem;">
                                        <p style="font-family: Pretendard, 'Apple SD Gothic Neo', 'Noto Sans KR', -apple-system, BlinkMacSystemFont, system-ui, sans-serif; font-size: 0.688rem; color: #94A3B8; line-height: 2.0; margin: 0; text-align: left; word-break: keep-all;">
                                            리에종 ㅣ 대표 : 주서영 ㅣ 개인정보관리책임자 : 권동민<br/>
                                            주소 : 서울특별시 종로구 127 ㅣ메일 : linkit@linkit.im<br/>
                                            Copyright ⓒ 2024. liaison All rights reserved.<br/>
                                            ※ 본 메일은 이메일 변경을 위해 발송되었습니다
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
                """, teamName, teamName, teamLogoImagePath, teamName, "www.linkit.im");

        mimeMessage.setContent(msgg, "text/html; charset=utf-8");
        mimeMessage.setFrom(mailId);

        return mimeMessage;
    }
}
