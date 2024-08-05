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

import java.io.UnsupportedEncodingException;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class MailServiceImpl implements MailService {
    // MailConfig에서 등록해둔 Bean을 autowired하여 사용하기
    private final JavaMailSender emailSender;

    // 사용자가 메일로 받을 인증번호
    private String key;

    @Value("${naver.id}")
    private String id;

    // 메일 발송
    // sendSimpleMessage 의 매개변수 to는 이메일 주소가 되고,
    // MimeMessage 객체 안에 내가 전송할 메일의 내용을 담는다
    // bean으로 등록해둔 javaMail 객체를 사용하여 이메일을 발송한다
    @Override
    public String sendSimpleMessage(String to) throws Exception {
        MimeMessage message = creatMessage(to); // "to" 로 메일 발송
        log.info("********생성된 메시지******** => " + message);

        // 예외처리
        try {
            // 이게 메일로 보내주는 메소드
            emailSender.send(message);
        } catch (Exception e) {
            e.printStackTrace();
            throw new IllegalArgumentException();
        }
        // 메일로 사용자에게 보낸 인증코드를 서버로 반환! 인증코드 일치여부를 확인하기 위함
        return key;
    }

    // 메일 내용 작성
    private MimeMessage creatMessage(String to) throws MessagingException, UnsupportedEncodingException {
        log.info("메일받을 사용자 : " + to);
        log.info("인증번호 : " + key);

        MimeMessage message = emailSender.createMimeMessage();
        message.addRecipients(Message.RecipientType.TO, to);
        // 이메일 제목
        message.setSubject("관리자 회원가입을 위한 이메일 인증코드");

        String msgg = "";
        msgg += "<div class=\"container\" style=\"font-family: 'Pretendard', sans-serif; margin: 0; padding: 0; box-sizing: border-box; background-color: #F0F2F6; display: flex; width: 100%; flex-direction: column; align-items: center; justify-content: center; padding-top: 5rem; padding-bottom: 5rem;\">";
        msgg += "    <div class=\"mail-wrapper\" style=\"display: flex; flex-direction: column; width: 40.125rem; padding: 3.75rem 1.25rem 5.625rem 1.25rem; gap: 3.125rem; background-color: white; align-items: flex-start;\">";
        msgg += "        <div class=\"center-content\" style=\"display: flex; flex-direction: column; justify-content: center; align-items: center; gap: 2.125rem; width: 100%;\">";
        msgg += "            <div class=\"left-content\" style=\"display: flex; flex-direction: column; align-items: flex-start; gap: 0.75rem; width: 100%;\">";
        msgg += "                <div class=\"logo\" style=\"display: flex; width: 5.75rem; height: 1.09525rem; justify-content: center; align-items: center;\">";
        msgg += "                    <img src=\"./linkit_color_logo.svg\" class=\"h-full object-contain\">";
        msgg += "                </div>";
        msgg += "                <div class=\"divider\" style=\"width: 100%; height: 0; border: 1px solid #CBD4E1;\"></div>";
        msgg += "            </div>";

        msgg += "            <div class=\"large-text\" style=\"text-align: center; font-size: 2.5rem; font-weight: 400; line-height: 1.5625rem; color: var(--, #000); font-feature-settings: 'liga' off, 'clig' off;\">";
        msgg += "                👋👋";
        msgg += "            </div>";
        msgg += "            <div style=\"display: flex; justify-content: center; width: 100%;\">";
        msgg += "                <div class=\"normal-text\" style=\"font-size: 0.875rem; font-weight: 400; line-height: 1.5625rem; color: var(--, #000); font-feature-settings: 'liga' off, 'clig' off; text-align: center;\">";
        msgg += "                    [내 이름]님 안녕하세요, <span class=\"blue-text\" style=\"font-size: 0.875rem; font-weight: 600; line-height: 1.5625rem; color: var(--Key-blue-key60, #2563EB); text-decoration: underline; font-feature-settings: 'liga' off, 'clig' off;\">링킷</span>";
        msgg += "                    입니다. <br>";
        msgg += "                    [내 이름]님이 정성스럽게 써주신 이력서를 통해 <br>";
        msgg += "                    [Sukki]님께서 <span class=\"bold-text\" style=\"font-size: 0.875rem; font-weight: 700; line-height: 1.5625rem; color: var(--, #000); font-feature-settings: 'liga' off, 'clig' off;\">7월";
        msgg += "                        12일</span>에 매칭 요청을 주셨습니다. <br>";
        msgg += "                    소개글을 확인하고 매칭 요청에 응답해 보세요!";
        msgg += "                </div>";
        msgg += "            </div>";

        msgg += "            <div class=\"divider\" style=\"width: 100%; height: 0; border: 1px solid #CBD4E1;\"></div>";
        msgg += "        </div>";

        msgg += "        <div class=\"profile-section\" style=\"display: flex; flex-direction: column; align-items: flex-start; gap: 1.125rem; width: 100%;\">";
        msgg += "            <div class=\"left-content\" style=\"display: flex; flex-direction: column; align-items: flex-start; gap: 0.75rem; width: 100%;\">";
        msgg += "                <div class=\"bold-text\" style=\"font-size: 1.875rem;\">📮</div>";
        msgg += "                <div class=\"bold-text\" style=\"font-size: 1.2rem;\">Sukki님의 소개글</div>";
        msgg += "                <div class=\"normal-text\" style=\"font-size: 0.875rem; font-weight: 400; line-height: 1.5625rem; color: var(--, #000); font-feature-settings: 'liga' off, 'clig' off;\">";
        msgg += "                    매칭 요청 본문";
        msgg += "                </div>";
        msgg += "            </div>";
        msgg += "        </div>";

        msgg += "        <div class=\"blue-bg\" style=\"display: flex; width: 37.625rem; padding: 0.625rem 1.25rem; align-items: flex-end; gap: 0.625rem; border-radius: 0.5rem; background-color: var(--Grey-scale-grey20, #F1F4F9);\">";
        msgg += "            <div class=\"history-section\" style=\"display: flex; flex-direction: column; justify-content: center; align-items: flex-start; gap: 0.625rem; flex: 1;\">";
        msgg += "                <div class=\"normal-text font-semibold\" style=\"font-size: 0.875rem; font-weight: 400; line-height: 1.5625rem; color: var(--, #000); font-feature-settings: 'liga' off, 'clig' off;\">";
        msgg += "                    Sukki님의 이력</div>";
        msgg += "                <div class=\"normal-text\" style=\"color: var(--Grey-scale-grey80, #27364B); line-height: 1.4375rem; font-size: 0.875rem; font-weight: 400; color: var(--, #000); font-feature-settings: 'liga' off, 'clig' off;\">";
        msgg += "                    홍익대학교 시각디자인과 재학 중 초기 창업팀 근무 경험(5개월)";
        msgg += "                </div>";
        msgg += "                <div class=\"profile-link\" style=\"display: flex; justify-content: flex-end; align-items: center; gap: 0.625rem; width: 100%;\">";
        msgg += "                    <div class=\"right-text\" style=\"text-align: right; font-size: 0.875rem; font-weight: 600; line-height: 1.5625rem; color: var(--, #000); font-feature-settings: 'liga' off, 'clig' off;\">";
        msgg += "                        프로필 보러가기</div>";
        msgg += "                </div>";
        msgg += "            </div>";
        msgg += "        </div>";

        msgg += "        <div class=\"response-section\" style=\"display: flex; justify-content: center; align-items: center; width: 100%; border-radius: 0.5rem; height: 3.4375rem; background-color: var(--Key-blue-key60, #2563EB);\">";
        msgg += "            <button class=\"response-button\" style=\"font-size: 1rem; font-weight: 600; color: var(--Grey-scale-grey00, #FFF); background-color: transparent; border: none; cursor: pointer; font-family: 'Pretendard', sans-serif; font-feature-settings: 'liga' off, 'clig' off; line-height: normal;\">";
        msgg += "                Sukki님에게 응답하기 ✍";
        msgg += "            </button>";
        msgg += "        </div>";

        msgg += "        <div class=\"center-content\" style=\"display: flex; flex-direction: column; justify-content: center; align-items: center; gap: 2.125rem; width: 100%;\">";
        msgg += "            <div class=\"left-content\" style=\"display: flex; flex-direction: column; align-items: flex-start; gap: 0.75rem; width: 100%;\">";
        msgg += "                <div class=\"logo\" style=\"display: flex; width: 5.75rem; height: 1.09525rem; justify-content: center; align-items: center;\">";
        msgg += "                    <img src=\"./linkit_grey_logo.svg\" class=\"h-full object-contain\">";
        msgg += "                </div>";
        msgg += "                <div class=\"divider\" style=\"width: 100%; height: 0; border: 1px solid #CBD4E1;\"></div>";
        msgg += "            </div>";
        msgg += "            <div class=\"footer-text\" style=\"font-size: 0.875rem; font-weight: 400; line-height: 1.5625rem; color: var(--Grey-scale-grey60, #64748B); font-feature-settings: 'liga' off, 'clig' off;\">";
        msgg += "                메일링 후 필요한 푸터 내용이 있으면 여기 넣어주세요";
        msgg += "            </div>";
        msgg += "            <div class=\"divider\" style=\"width: 100%; height: 0; border: 1px solid #CBD4E1;\"></div>";
        msgg += "        </div>";
        msgg += "    </div>";
        msgg += "</div>";

        // 메일 내용, charset타입, subtype
        message.setText(msgg, "utf-8", "html");

        // 보내는 사람의 이메일 주소, 보내는 사람 이름
        message.setFrom(id);
        log.info("********creatMessage 함수에서 생성된 msg 메시지********" + msgg);
        log.info("********creatMessage 함수에서 생성된 리턴 메시지********" + message);

        return message;
    }

}
