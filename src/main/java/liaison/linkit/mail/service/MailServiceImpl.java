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

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class MailServiceImpl implements MailService {
    private final JavaMailSender emailSender;

    @Value("${naver.id}")
    private String id;

    // 1. 내 이력서 -> 내 이력서 매칭 요청 보낸 경우
    @Override
    public void mailPrivateToPrivate(
            final String receiverName,
            final String senderName,
            final LocalDateTime requestDate,
            final String requestMessage
    ) throws Exception {
        final MimeMessage mimeMessage = createPrivateToPrivateMail(
                receiverName,
                senderName,
                requestDate,
                requestMessage
        );

        try {
            emailSender.send(mimeMessage);
        } catch (Exception e) {
            e.printStackTrace();
            throw new IllegalArgumentException();
        }
    }

    private MimeMessage createPrivateToPrivateMail(
            final String receiverEmail,
            final String senderName,
            final LocalDateTime requestDate,
            final String requestMessage
    ) throws MessagingException {
        final MimeMessage mimeMessage = emailSender.createMimeMessage();
        mimeMessage.addRecipients(Message.RecipientType.TO, receiverEmail);
        mimeMessage.setSubject("[링킷] 내 이력서 매칭 요청 알림");

        final String msgg = String.format("""
                <div class="container" style="font-family: 'Pretendard', sans-serif; margin: 0; padding: 0; box-sizing: border-box; background-color: #F0F2F6; display: flex; width: 100%%; flex-direction: column; align-items: center; justify-content: center; padding-top: 5rem; padding-bottom: 5rem;">
                    <div class="mail-wrapper" style="display: flex; flex-direction: column; width: 40.125rem; padding: 3.75rem 1.25rem 5.625rem 1.25rem; gap: 3.125rem; background-color: white; align-items: flex-start;">
                        <div class="center-content" style="display: flex; flex-direction: column; justify-content: center; align-items: center; gap: 2.125rem; width: 100%%;">
                            <div class="left-content" style="display: flex; flex-direction: column; align-items: flex-start; gap: 0.75rem; width: 100%%;">
                                <div class="logo" style="display: flex; width: 5.75rem; height: 1.09525rem; justify-content: center; align-items: center;">
                                    <img src="https://image-prod.linkit.im/mail/linkit_color_logo.svg" class="h-full object-contain">
                                </div>
                                <div class="divider" style="width: 100%%; height: 0; border: 1px solid #CBD4E1;"></div>
                            </div>

                            <div class="large-text" style="text-align: center; font-size: 2.5rem; font-weight: 400; line-height: 1.5625rem; color: var(--, #000); font-feature-settings: 'liga' off, 'clig' off;">
                                👋👋
                            </div>
                            <div style="display: flex; justify-content: center; width: 100%%;">
                                <div class="normal-text" style="font-size: 0.875rem; font-weight: 400; line-height: 1.5625rem; color: var(--, #000); font-feature-settings: 'liga' off, 'clig' off; text-align: center;">
                                    %s님 안녕하세요, <span class="blue-text" style="font-size: 0.875rem; font-weight: 600; line-height: 1.5625rem; color: var(--Key-blue-key60, #2563EB); text-decoration: underline; font-feature-settings: 'liga' off, 'clig' off;">링킷</span>
                                    입니다. <br>
                                    %s님이 정성스럽게 써주신 이력서를 통해 <br>
                                    %s님께서 <span class="bold-text" style="font-size: 0.875rem; font-weight: 700; line-height: 1.5625rem; color: var(--, #000); font-feature-settings: 'liga' off, 'clig' off;">%s</span>에 매칭 요청을 주셨습니다. <br>
                                    소개글을 확인하고 매칭 요청에 응답해 보세요!
                                </div>
                            </div>

                            <div class="divider" style="width: 100%%; height: 0; border: 1px solid #CBD4E1;"></div>
                        </div>

                        <div class="profile-section" style="display: flex; flex-direction: column; align-items: flex-start; gap: 1.125rem; width: 100%%;">
                            <div class="left-content" style="display: flex; flex-direction: column; align-items: flex-start; gap: 0.75rem; width: 100%%;">
                                <div class="bold-text" style="font-size: 1.875rem;">📮</div>
                                <div class="bold-text" style="font-size: 1.2rem;">%s님의 소개글</div>
                                <div class="normal-text" style="font-size: 0.875rem; font-weight: 400; line-height: 1.5625rem; color: var(--, #000); font-feature-settings: 'liga' off, 'clig' off;">
                                    %s
                                </div>
                            </div>
                        </div>

                        <div class="blue-bg" style="display: flex; width: 37.625rem; padding: 0.625rem 1.25rem; align-items: flex-end; gap: 0.625rem; border-radius: 0.5rem; background-color: var(--Grey-scale-grey20, #F1F4F9);">
                            <div class="history-section" style="display: flex; flex-direction: column; justify-content: center; align-items: flex-start; gap: 0.625rem; flex: 1;">
                                <div class="normal-text font-semibold" style="font-size: 0.875rem; font-weight: 400; line-height: 1.5625rem; color: var(--, #000); font-feature-settings: 'liga' off, 'clig' off;">
                                    %s님의 이력</div>
                                <div class="normal-text" style="color: var(--Grey-scale-grey80, #27364B); line-height: 1.4375rem; font-size: 0.875rem; font-weight: 400; color: var(--, #000); font-feature-settings: 'liga' off, 'clig' off;">
                                    이력 설명
                                </div>
                                <div class="profile-link" style="display: flex; justify-content: flex-end; align-items: center; gap: 0.625rem; width: 100%%;">
                                    <div class="right-text" style="text-align: right; font-size: 0.875rem; font-weight: 600; line-height: 1.5625rem; color: var(--, #000); font-feature-settings: 'liga' off, 'clig' off;">
                                        프로필 보러가기</div>
                                </div>
                            </div>
                        </div>

                        <div class="response-section" style="display: flex; justify-content: center; align-items: center; width: 100%%; border-radius: 0.5rem; height: 3.4375rem; background-color: var(--Key-blue-key60, #2563EB);">
                            <button class="response-button" style="font-size: 1rem; font-weight: 600; color: var(--Grey-scale-grey00, #FFF); background-color: transparent; border: none; cursor: pointer; font-family: 'Pretendard', sans-serif; font-feature-settings: 'liga' off, 'clig' off; line-height: normal;">
                                %s님에게 응답하기 ✍
                            </button>
                        </div>

                        <div class="center-content" style="display: flex; flex-direction: column; justify-content: center; align-items: center; gap: 2.125rem; width: 100%%;">
                            <div class="left-content" style="display: flex; flex-direction: column; align-items: flex-start; gap: 0.75rem; width: 100%%;">
                                <div class="logo" style="display: flex; width: 5.75rem; height: 1.09525rem; justify-content: center; align-items: center;">
                                    <img src="https://image-prod.linkit.im/mail/linkit_grey_logo.svg" class="h-full object-contain">
                                </div>
                                <div class="divider" style="width: 100%%; height: 0; border: 1px solid #CBD4E1;"></div>
                            </div>
                            <div class="footer-text" style="font-size: 0.875rem; font-weight: 400; line-height: 1.5625rem; color: var(--Grey-scale-grey60, #64748B); font-feature-settings: 'liga' off, 'clig' off;">
                                메일링 후 필요한 푸터 내용이 있으면 여기 넣어주세요
                            </div>
                            <div class="divider" style="width: 100%%; height: 0; border: 1px solid #CBD4E1;"></div>
                        </div>
                    </div>
                </div>
                """, receiverEmail, receiverEmail, senderName, requestDate, senderName, requestMessage, senderName, senderName);
        mimeMessage.setText(msgg, "utf-8", "html");
        mimeMessage.setFrom(id);

        return mimeMessage;
    }
}
