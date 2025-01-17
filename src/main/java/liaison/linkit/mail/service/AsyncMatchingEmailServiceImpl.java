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
                <!DOCTYPE html>
                <html>
                <head>
                  <meta charset="UTF-8">
                  <title>매칭 성사 알림</title>
                </head>
                <body style="margin:0; padding:0; background-color:#ffffff;">
                  <table border="0" cellpadding="0" cellspacing="0" width="100%%" style="border-collapse:collapse; background-color:#ffffff;">
                    <tbody>
                      <tr>
                        <td align="center" style="padding:0; margin:0;">
                          <!-- 메인 컨테이너 테이블 시작 -->
                          <table border="0" cellpadding="0" cellspacing="0" width="600" style="border-collapse:collapse; background-color:#F1F4F9;">
                            <tbody>
                              <!-- 헤더 -->
                              <tr>
                                <td align="left" style="padding:20px; background-color:#FFFFFF;">
                                  <img src="https://image-prod.linkit.im/mail/linkit_color_logo.png" alt="Logo" style="display:block; width:92px; height:auto;">
                                </td>
                              </tr>
                              <!-- 구분선 -->
                              <tr>
                                <td style="height:1px; background-color:#CBD4E1; line-height:1px; font-size:1px;">&nbsp;</td>
                              </tr>
                             \s
                              <!-- 메인 콘텐츠 -->
                              <tr>
                                <td style="padding:30px 20px; text-align:center; background-color:#F1F4F9;">
                                  <h1 style="margin:0 0 16px 0; font-family:Arial, sans-serif; font-size:20px; font-weight:bold; color:#2563EB;">
                                    '%s'님과 매칭 성사!
                                  </h1>
                                  <p style="margin:0; font-family:Arial, sans-serif; font-size:12px; line-height:1.6; color:#64748B;">
                                    매칭이 성사되었어요.<br>
                                    채팅을 통해 이야기를 나눠 보세요!
                                  </p>
                                </td>
                              </tr>
                
                              <!-- 프로필 카드 -->
                              <tr>
                                <!-- 1) 부모 셀에 중앙 정렬 -->
                                <td align="center" style="padding: 0 20px 30px; background-color: #F1F4F9;">
                                  <!-- 2) 프로필 카드 테이블 (width 고정) -->
                                  <table\s
                                    cellpadding="0"\s
                                    cellspacing="0"\s
                                    border="0"\s
                                    width="300"\s
                                    style="border-collapse:collapse; background:#EDF3FF; border:1px solid #CBD4E1; border-radius:12px;"
                                  >
                                    <tr>
                                      <td style="padding:24px;">
                                        <!-- 내부 컨텐츠를 담을 테이블 -->
                                        <table\s
                                          cellpadding="0"\s
                                          cellspacing="0"\s
                                          border="0"\s
                                          width="100%%"\s
                                          style="border-collapse:collapse;"
                                        >
                                          <tr>
                                            <!-- 3) 왼쪽: 이미지 셀 -->
                                            <td
                                              align="center"
                                              valign="middle"
                                              width="80"
                                              style="vertical-align: middle; text-align: center;"
                                            >
                                              <img\s
                                                src="%s"\s
                                                alt="프로필 이미지"
                                                style="width:72px; height:72px; border-radius:15px; display:block;"
                                              >
                                            </td>
                
                                            <!-- 4) 오른쪽: 텍스트 셀 -->
                                            <td
                                              valign="middle"
                                              style="vertical-align: middle; padding-left:16px;"
                                            >
                                              <p\s
                                                style="margin:0 0 8px 0; font-family:Arial, sans-serif; font-size:18px; font-weight:bold; color:#000000;"
                                              >
                                                %s <!-- 이름 -->
                                              </p>
                                              <table
                                                cellpadding="0"
                                                cellspacing="0"
                                                border="0"
                                                style="border-collapse:collapse; font-family:Arial, sans-serif; font-size:12px; color:#94A3B8;"
                                              >
                                                <tr>
                                                  <td style="padding-bottom:4px; width:40px;">포지션</td>
                                                  <td style="padding-bottom:4px;">| %s</td>
                                                </tr>
                                                <tr>
                                                  <td style="width:40px;">지역</td>
                                                  <td>| %s</td>
                                                </tr>
                                              </table>
                                            </td>
                                          </tr>
                                        </table>
                                        <!-- 내부 컨텐츠 테이블 끝 -->
                                      </td>
                                    </tr>
                                  </table>
                                  <!-- 프로필 카드 테이블 끝 -->
                                </td>
                              </tr>
                
                
                              <!-- 채팅하기 버튼 -->
                              <tr>
                                <td align="center" style="padding:0 20px 30px; background-color:#F1F4F9;">
                                  <a href="https://www.linkit.im"\s
                                     style="display:inline-block; padding:12px 24px; background-color:#2563EB; color:#FFFFFF; text-decoration:none; border-radius:24px; font-family:Arial, sans-serif; font-size:14px;">
                                    <img src="https://image-prod.linkit.im/mail/chat_icon_image.png" alt="채팅"\s
                                         style="width:20px; height:20px; vertical-align:middle; margin-right:8px;">
                                    채팅하기
                                  </a>
                                </td>
                              </tr>
                
                              <!-- 주의사항 -->
                              <tr>
                                <td style="padding:0 20px 20px; background-color:#F1F4F9;">
                                  <p style="margin:0; font-family:Arial, sans-serif; font-size:12px; color:#94A3B8; line-height:1.4;">
                                    *본인이 아닌 경우 이메일을 삭제해 주세요
                                  </p>
                                </td>
                              </tr>
                
                              <!-- 구분선 -->
                              <tr>
                                <td style="height:1px; background-color:#CBD4E1; line-height:1px; font-size:1px;">&nbsp;</td>
                              </tr>
                
                              <!-- 푸터 -->
                              <tr>
                                <td style="background-color:#FFFFFF; padding:20px;">
                                  <img src="https://image-prod.linkit.im/mail/linkit_grey_logo.png" alt="Logo"\s
                                       style="display:block; width:92px; height:auto; margin-bottom:20px;">
                                  <p style="margin:0; font-family:Arial, sans-serif; font-size:11px; color:#94A3B8; line-height:1.8;">
                                    리에종 ㅣ 대표 : 주서영 ㅣ 개인정보관리책임자 : 권동민<br>
                                    주소 : 서울특별시 종로구 127 ㅣ메일 : linkit@linkit.im<br>
                                    Copyright ⓒ 2024. liaison All rights reserved.<br>
                                    ※ 본 메일은 매칭 성사 알림을 위해 발송되었습니다
                                  </p>
                                </td>
                              </tr>
                
                              <!-- 구분선 -->
                              <tr>
                                <td style="height:1px; background-color:#CBD4E1; line-height:1px; font-size:1px;">&nbsp;</td>
                              </tr>
                            </tbody>
                          </table>
                          <!-- 메인 컨테이너 테이블 끝 -->
                        </td>
                      </tr>
                    </tbody>
                  </table>
                </body>
                </html>
                
                """, otherPartyName, otherPartyLogoImagePath, otherPartyName, otherPartyPositionOrTeamSize, otherPartyRegionDetail);

        mimeMessage.setContent(msgg, "text/html; charset=utf-8");
        mimeMessage.setFrom(mailId);

        return mimeMessage;
    }
}
