package liaison.linkit.mail.service;

import java.io.UnsupportedEncodingException;
import java.util.List;

import jakarta.mail.Message;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.InternetAddress;
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
public class AsyncAnnouncementAdvertiseEmailServiceImpl
        implements AsyncAnnouncementAdvertiseEmailService {

    private final JavaMailSender javaMailSender;

    @Value("${google.id}")
    private String mailId;

    @Override
    public void sendAnnouncementAdvertiseEmail(
            final String receiverMailAddress,
            final String announcementMajorPositionName,
            final String announcementMinorPositionName, // 추가: 포지션 소분류
            final String teamCode,
            final String teamLogoImagePath,
            final String teamName,
            final String announcementTitle,
            final List<String> announcementSkillNames,
            final Long announcementId)
            throws MessagingException, UnsupportedEncodingException {
        final MimeMessage mimeMessage =
                createAnnouncementAdvertiseEmail(
                        receiverMailAddress,
                        announcementMajorPositionName,
                        announcementMinorPositionName, // 추가: 포지션 소분류 전달
                        teamCode,
                        teamLogoImagePath,
                        teamName,
                        announcementTitle,
                        announcementSkillNames,
                        announcementId);

        try {
            javaMailSender.send(mimeMessage);
        } catch (Exception e) {
            e.printStackTrace();
            throw new IllegalArgumentException();
        }
    }

    private MimeMessage createAnnouncementAdvertiseEmail(
            final String receiverMailAddress,
            final String announcementMajorPositionName,
            final String announcementMinorPositionName, // 추가: 포지션 소분류
            final String teamCode,
            final String teamLogoImagePath,
            final String teamName,
            final String announcementTitle,
            final List<String> announcementSkillNames,
            final Long announcementId)
            throws MessagingException, UnsupportedEncodingException {

        final MimeMessage mimeMessage = javaMailSender.createMimeMessage();
        mimeMessage.addRecipients(Message.RecipientType.TO, receiverMailAddress);
        mimeMessage.setSubject("[링킷] 신규 모집 공고 알림 발송");

        // 팀 로고 이미지 처리 - NULL인 경우 기본 이미지 사용
        final String logoImageUrl =
                (teamLogoImagePath != null && !teamLogoImagePath.isEmpty())
                        ? teamLogoImagePath
                        : "https://image-prod.linkit.im/mail/linkit_grey_box_logo.png";

        // 지원하기 버튼 URL 생성
        final String recruitUrl =
                String.format("https://www.linkit.im/team/%s/recruit/%d", teamCode, announcementId);

        // 메인 홈페이지 URL
        final String mainUrl = "https://www.linkit.im";

        // 포지션 표시 섹션 - 더 진한 배경색과 더 진한 파란색 글자
        final String positionSection =
                String.format(
                        """
                                <div style="margin-top: 16px;">
                                  <span style="display: inline-block; padding: 6px 12px; background-color: #D3E1FE; border-radius: 6px; margin-right: 8px; font-family: Pretendard, 'Apple SD Gothic Neo', 'Noto Sans KR', -apple-system, BlinkMacSystemFont, system-ui, sans-serif; font-size: 12px; color: #2563EB; font-weight: 500;">%s</span>
                                  <span style="display: inline-block; padding: 6px 12px; background-color: #D3E1FE; border-radius: 6px; font-family: Pretendard, 'Apple SD Gothic Neo', 'Noto Sans KR', -apple-system, BlinkMacSystemFont, system-ui, sans-serif; font-size: 12px; color: #2563EB; font-weight: 500;">%s</span>
                                </div>
                                """,
                        announcementMajorPositionName, announcementMinorPositionName);

        // 스킬 태그 처리 로직
        String skillsSection = "";

        if (announcementSkillNames != null && !announcementSkillNames.isEmpty()) {
            StringBuilder skillsSectionBuilder = new StringBuilder();
            skillsSectionBuilder.append("<div style=\"margin-top: 16px;\">");

            // 최대 2개 스킬만 표시
            int displayCount = Math.min(2, announcementSkillNames.size());

            // 표시할 스킬들 추가 - 통일된 UI 적용
            for (int i = 0; i < displayCount; i++) {
                skillsSectionBuilder.append(
                        String.format(
                                "<span style=\"display: inline-block; padding: 6px 12px; background-color: #EBF1FF; border-radius: 6px; margin-right: 8px; font-family: Pretendard, 'Apple SD Gothic Neo', 'Noto Sans KR', -apple-system, BlinkMacSystemFont, system-ui, sans-serif; font-size: 12px; color: #2563EB;\">%s</span>",
                                announcementSkillNames.get(i)));
            }

            // 추가 스킬이 있는 경우 +n 표시 - 동일한 UI 스타일 적용
            if (announcementSkillNames.size() > 2) {
                int remainingCount = announcementSkillNames.size() - 2;
                skillsSectionBuilder.append(
                        String.format(
                                "<span style=\"display: inline-block; padding: 6px 12px; background-color: #EBF1FF; border-radius: 6px; font-family: Pretendard, 'Apple SD Gothic Neo', 'Noto Sans KR', -apple-system, BlinkMacSystemFont, system-ui, sans-serif; font-size: 12px; color: #2563EB;\">+%d</span>",
                                remainingCount));
            }

            skillsSectionBuilder.append("</div>");
            skillsSection = skillsSectionBuilder.toString();
        }
        // 스킬이 없는 경우 빈 문자열 유지 (스킬 섹션 표시 안 함)

        final String msgg =
                String.format(
                        """
                                <table border="0" cellpadding="0" cellspacing="0" width="100%%" style="border-collapse:collapse; background-color: #ffffff;">
                                    <tbody>
                                      <tr>
                                        <td>
                                          <table align="center" width="100%%" cellspacing="0" cellpadding="0" border="0" bgcolor="#F1F4F9" style="max-width: 642px; margin: 0 auto;">
                                            <tbody>
                                              <tr>
                                                <td align="left" style="padding: 20px; background-color: #FFFFFF;">
                                                  <a href="%s" target="_blank">
                                                    <img src="https://image-prod.linkit.im/mail/linkit_color_logo.png" alt="Logo" style="display: block; width: 92px; height: auto;">
                                                  </a>
                                                </td>
                                              </tr>
                                              <tr>
                                                <td style="background-color: #CBD4E1; height: 1px;"></td>
                                              </tr>
                                              <tr>
                                                <td style="padding: 30px 20px; text-align: center;">
                                                  <h1 style="font-family: Pretendard, 'Apple SD Gothic Neo', 'Noto Sans KR', -apple-system, BlinkMacSystemFont, system-ui, sans-serif; font-size: 20px; font-weight: 600; margin: 0 0 16px 0; word-break: keep-all;"><span style="color: #2563EB;">%s</span> <span style="color: #27364B;">새로운 모집 공고</span></h1>
                                                  <p style="font-family: Pretendard, 'Apple SD Gothic Neo', 'Noto Sans KR', -apple-system, BlinkMacSystemFont, system-ui, sans-serif; font-size: 12px; color: #64748B; line-height: 1.4; margin: 0; word-break: keep-all;">
                                                    %s 포지션의 공고가 업로드 되었어요<br/>
                                                    공고를 확인하고 지원해 보세요!
                                                  </p>
                                                </td>
                                              </tr>
                                              <tr>
                                                <td style="padding: 0 20px;">
                                                  <table align="center" border="0" cellpadding="0" cellspacing="0" width="90%%" bgcolor="#FFFFFF" style="border: 1px solid #2563EB; border-radius: 12px; max-width: 500px;">
                                                    <tr>
                                                      <td style="padding: 24px 24px;">
                                                        <table border="0" cellpadding="0" cellspacing="0" width="100%%">
                                                          <tr>
                                                            <td style="vertical-align: top;">
                                                              <table border="0" cellpadding="0" cellspacing="0" width="100%%">
                                                                <tr>
                                                                  <td width="40px" style="vertical-align: middle;">
                                                                    <table cellpadding="0" cellspacing="0" border="0" style="border-collapse: collapse;">
                                                                      <tr>
                                                                        <td style="width: 40px; height: 40px; background-color: #FFFFFF; border-radius: 6px; text-align: center; vertical-align: middle;">
                                                                          <img src="%s" alt="팀 로고" style="width: 24px; height: 24px; display: inline-block;">
                                                                        </td>
                                                                      </tr>
                                                                    </table>
                                                                  </td>
                                                                  <td style="vertical-align: middle;">
                                                                    <p style="font-family: Pretendard, 'Apple SD Gothic Neo', 'Noto Sans KR', -apple-system, BlinkMacSystemFont, system-ui, sans-serif; font-size: 16px; font-weight: 600; margin: 0; color: #27364B;">%s</p>
                                                                  </td>
                                                                </tr>
                                                              </table>

                                                              <p style="font-family: Pretendard, 'Apple SD Gothic Neo', 'Noto Sans KR', -apple-system, BlinkMacSystemFont, system-ui, sans-serif; font-size: 16px; font-weight: 600; color: #27364B; margin: 16px 0 0 0;">%s</p>
                                                              %s
                                                              %s
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
                                                  <a href="%s" style="display: inline-block; padding: 12px 24px; background-color: #2563EB; color: #FFFFFF; text-decoration: none; border-radius: 24px; min-width: 175px; font-family: Pretendard, 'Apple SD Gothic Neo', 'Noto Sans KR', -apple-system, BlinkMacSystemFont, system-ui, sans-serif; font-weight: 500; font-size: 16px;">
                                                    공고 지원하기
                                                  </a>
                                                </td>
                                              </tr>
                                              <tr>
                                                <td style="padding: 0 20px 30px;">
                                                </td>
                                              </tr>
                                              <tr>
                                                <td style="background-color: #CBD4E1; height: 1px;"></td>
                                              </tr>
                                              <tr>
                                                <td style="padding: 20px; background-color: #FFFFFF;">
                                                  <a href="%s" target="_blank">
                                                    <img src="https://image-prod.linkit.im/mail/linkit_grey_logo.png" alt="Logo" style="display: block; width: 92px; height: auto; margin-bottom: 20px;">
                                                  </a>
                                                  <p style="font-family: Pretendard, 'Apple SD Gothic Neo', 'Noto Sans KR', -apple-system, BlinkMacSystemFont, system-ui, sans-serif; font-size: 11px; color: #94A3B8; line-height: 2.0; margin: 0; text-align: left; word-break: keep-all;">
                                                    리에종 ㅣ 대표 : 주서영 ㅣ 개인정보관리책임자 : 권동민<br/>
                                                    주소 : 서울특별시 종로구 127 ㅣ메일 : linkit@linkit.im<br/>
                                                    Copyright ⓒ 2025. liaison All rights reserved.<br/>
                                                    ※ 본 메일은 모집 공고 알림을 위해 발송되었습니다
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
                                """,
                        mainUrl,
                        teamName,
                        announcementMajorPositionName,
                        logoImageUrl,
                        teamName,
                        announcementTitle,
                        positionSection,
                        skillsSection,
                        recruitUrl,
                        mainUrl);

        mimeMessage.setContent(msgg, "text/html; charset=utf-8");
        mimeMessage.setFrom(new InternetAddress(mailId, "링킷(Linkit)", "UTF-8"));

        return mimeMessage;
    }
}
