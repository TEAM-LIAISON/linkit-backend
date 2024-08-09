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
import java.time.format.DateTimeFormatter;
import java.util.Locale;

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
    public void mailPrivateToPrivate(final String receiverEmail, final String receiverName, final String senderName, final LocalDateTime requestDate, final String requestMessage) throws Exception {
        final MimeMessage mimeMessage = createPrivateToPrivateMail(receiverEmail, receiverName, senderName, requestDate, requestMessage);

        try {
            emailSender.send(mimeMessage);
        } catch (Exception e) {
            e.printStackTrace();
            throw new IllegalArgumentException();
        }
    }

    // 2. 팀 소개서 -> 내 이력서 매칭 요청 보낸 경우
    @Override
    public void mailTeamToPrivate(final String receiverEmail, final String receiverName, final String senderName, final LocalDateTime requestDate, final String requestMessage) throws Exception {
        final MimeMessage mimeMessage = createTeamToPrivateMail(receiverEmail, receiverName, senderName, requestDate, requestMessage);

        try {
            emailSender.send(mimeMessage);
        } catch (Exception e) {
            e.printStackTrace();
            throw new IllegalArgumentException();
        }
    }

    // 3. 내 이력서 -> 팀 소개서로 매칭 요청 보낸 경우
    @Override
    public void mailPrivateToTeam(final String receiverEmail, final String receiverName, final String senderName, final LocalDateTime requestDate, final String requestMessage) throws Exception {
        final MimeMessage mimeMessage = createPrivateToTeamMail(receiverEmail, receiverName, senderName, requestDate, requestMessage);

        try {
            emailSender.send(mimeMessage);
        } catch (Exception e) {
            e.printStackTrace();
            throw new IllegalArgumentException();
        }
    }

    // 4. 팀 소개서 -> 팀 소개서로 매칭 요청 보낸 경우
    @Override
    public void mailTeamToTeam(final String receiverEmail, final String receiverName, final String senderName, final LocalDateTime requestDate, final String requestMessage) throws Exception {
        final MimeMessage mimeMessage = createTeamToTeamMail(receiverEmail, receiverName, senderName, requestDate, requestMessage);

        try {
            emailSender.send(mimeMessage);
        } catch (Exception e) {
            e.printStackTrace();
            throw new IllegalArgumentException();
        }
    }



    // 1.
    private MimeMessage createPrivateToPrivateMail(final String receiverEmail, final String receiverName, final String senderName, final LocalDateTime requestDate, final String requestMessage) throws MessagingException {
        final MimeMessage mimeMessage = emailSender.createMimeMessage();

        mimeMessage.addRecipients(Message.RecipientType.TO, receiverEmail);
        mimeMessage.setSubject("[링킷] 내 이력서 매칭 요청 알림");

        // DateTimeFormatter를 사용하여 날짜를 원하는 형식으로 포맷
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("M월 d일", Locale.KOREAN);
        String formattedDate = requestDate.format(formatter);

        final String msgg = String.format("""
            <table align="center" width="100%%" cellspacing="0" cellpadding="0" border="0"
                style="border-collapse: collapse;">
                <tbody>
                    <tr>
                        <td>
                            <table align="center" width="100%%" cellspacing="0" cellpadding="0" border="0" bgcolor="#ffffff"
                                style="max-width:650px; border-radius: 8px; overflow: hidden; margin: 0 auto;">
                                <tbody>
                                    <!-- Header Section -->
                                    <tr>
                                        <td align="left" style="padding: 20px;">
                                            <img src="https://image-prod.linkit.im/mail/linkit_color_logo.png" alt="Logo"
                                                style="display: block; width: 92px; height: auto;">
                                        </td>
                                    </tr>

                                    <!-- Divider -->
                                    <tr>
                                        <td style="background-color: #CBD4E1; height: 1px;"></td>
                                    </tr>

                                    <!-- Greeting Section -->
                                    <tr>
                                        <td align="center" style="padding: 30px 20px;">
                                            <span style="font-size: 2.5rem; font-weight: 400; color: #000;">👋👋</span>
                                                <p
                                                    style="font-size: 0.875rem; font-weight: 400; line-height: 1.5; color: #000; text-align: center;">
                                                    %s님 안녕하세요, <a href="https://linkit.im"
                                                        style="font-weight: 600; color: #2563EB; text-decoration: underline;">
                                                        링킷</a>입니다.<br />
                                                    %s님이 정성스럽게 써주신 이력서를 통해 <br />
                                                    %s님께서 <span style="font-weight: 700;">%s</span>에 매칭 요청을 주셨습니다. <br />
                                                    소개글을 확인하고 매칭 요청에 응답해 보세요!
                                                </p>
                                        </td>
                                    </tr>

                                    <!-- Divider -->
                                    <tr>
                                        <td style="background-color: #CBD4E1; height: 1px;"></td>
                                    </tr>

                                    <!-- Profile Section -->
                                    <tr>
                                        <td style="padding: 30px 20px;">
                                            <table width="100%%" cellspacing="0" cellpadding="0" border="0">
                                                <tbody>
                                                    <tr>
                                                        <td style="font-size: 1.875rem; font-weight: bold;">📮</td>
                                                    </tr>
                                                    <tr>
                                                        <td style="font-size: 1.2rem; font-weight: bold; padding-top: 10px;">%s님의
                                                            소개글</td>
                                                    </tr>
                                                    <tr>
                                                        <td
                                                            style="font-size: 0.875rem; font-weight: 400; line-height: 1.5; color: #000; padding-top: 10px;">
                                                            %s
                                                        </td>
                                                    </tr>
                                                </tbody>
                                            </table>
                                        </td>
                                    </tr>

                                    <!-- Blue Background Section -->
                                    <tr>
                                        <td style="padding: 0 20px;">
                                            <table width="100%%" cellspacing="0" cellpadding="0" border="0"
                                                style="background-color: #F1F4F9; border-radius: 0.5rem; padding: 10px 20px; box-sizing: border-box;">
                                                <tbody>
                                                    <tr>
                                                        <td style="font-size: 0.875rem; font-weight: 600; padding-bottom: 10px;">
                                                            %s님의 이력
                                                        </td>
                                                    </tr>
                                                    <tr>
                                                        <td
                                                            style="font-size: 0.875rem; font-weight: 400; line-height: 1.5; color: #27364B; padding-bottom: 10px;">
                                                            이력 설명 자리입니다.
                                                        </td>
                                                    </tr>
                                                    <tr>
                                                        <td align="right"
                                                            style="font-size: 0.875rem; font-weight: 600; color: #000;">
                                                            <a href="https://linkit.im" style="text-decoration: none; color: #000;">
                                                                프로필 보러가기
                                                                <svg xmlns="http://www.w3.org/2000/svg" width="5" height="8"
                                                                    viewBox="0 0 5 8" fill="none" style="vertical-align: middle;">
                                                                    <path d="M1 1.02344L4 4.02344L1 7.02344" stroke="black"
                                                                        stroke-width="1.5" stroke-linecap="round"
                                                                        stroke-linejoin="round" />
                                                                </svg>
                                                            </a>
                                                        </td>
                                                    </tr>
                                                </tbody>
                                            </table>
                                        </td>
                                    </tr>

                                    <!-- Response Button -->
                                    <tr>
                                        <td align="center" style="padding: 30px 20px;">
                                            <table width="100%%" cellspacing="0" cellpadding="0" border="0">
                                                <tbody>
                                                    <tr>
                                                        <td align="center"
                                                            style="background-color: #2563EB; border-radius: 0.5rem; height: 55px;">
                                                            <a href="https://linkit.im"
                                                                style="display: block; font-size: 1rem; font-weight: 600; color: #FFF; text-decoration: none; line-height: 55px;">
                                                                %s님에게 응답하기 ✍
                                                            </a>
                                                        </td>
                                                    </tr>
                                                </tbody>
                                            </table>
                                        </td>
                                    </tr>

                                    <!-- Footer Section -->
                                    <tr>
                                        <td align="left" style="padding: 30px 20px;">
                                            <img src="https://image-prod.linkit.im/mail/linkit_grey_logo.png" alt="Logo"
                                                style="display: block; width: 92px; height: auto;">
                                        </td>
                                    </tr>
                                    <tr>
                                        <td style="background-color: #CBD4E1; height: 1px;"></td>
                                    </tr>
                                    <tr>
                                        <td align="center"
                                            style="font-size: 0.875rem; font-weight: 400; line-height: 1.5; color: #64748B; padding-top: 20px; padding-bottom: 20px;">
                                            메일링 후 필요한 푸터 내용이 있으면 여기 넣어주세요
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
                receiverName,
                receiverName,
                senderName,
                requestDate.format(DateTimeFormatter.ofPattern("M월 d일")), // Formatting date as "8월 6일"
                senderName,
                requestMessage,
                senderName,
                senderName
        );

        mimeMessage.setContent(msgg, "text/html; charset=utf-8");
        mimeMessage.setFrom(id);

        return mimeMessage;
    }

    // 2. 팀 소개서 -> 내 이력서 매칭 요청 보낸 경우
    private MimeMessage createTeamToPrivateMail(final String receiverEmail, final String receiverName, final String senderName, final LocalDateTime requestDate, final String requestMessage) throws MessagingException {
        final MimeMessage mimeMessage = emailSender.createMimeMessage();
        mimeMessage.addRecipients(Message.RecipientType.TO, receiverEmail);
        mimeMessage.setSubject("[링킷] 내 이력서 매칭 요청 알림");
        // DateTimeFormatter를 사용하여 날짜를 원하는 형식으로 포맷
        final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("M월 d일", Locale.KOREAN);
        final String formattedDate = requestDate.format(formatter);

        final String msgg = String.format("""
            <table align="center" width="100%%" cellspacing="0" cellpadding="0" border="0"
                style="border-collapse: collapse;">
                <tbody>
                    <tr>
                        <td>
                            <table align="center" width="100%%" cellspacing="0" cellpadding="0" border="0" bgcolor="#ffffff"
                                style="max-width:650px; border-radius: 8px; overflow: hidden; margin: 0 auto;">
                                <tbody>
                                    <!-- Header Section -->
                                    <tr>
                                        <td align="left" style="padding: 20px;">
                                            <img src="https://image-prod.linkit.im/mail/linkit_color_logo.png" alt="Logo"
                                                style="display: block; width: 92px; height: auto;">
                                        </td>
                                    </tr>

                                    <!-- Divider -->
                                    <tr>
                                        <td style="background-color: #CBD4E1; height: 1px;"></td>
                                    </tr>

                                    <!-- Greeting Section -->
                                    <tr>
                                        <td align="center" style="padding: 30px 20px;">
                                            <span style="font-size: 2.5rem; font-weight: 400; color: #000;">🚀</span>
                                                <p
                                                    style="font-size: 0.875rem; font-weight: 400; line-height: 1.5; color: #000; text-align: center;">
                                                    %s님 안녕하세요, <a href="https://linkit.im"
                                                        style="font-weight: 600; color: #2563EB; text-decoration: underline;">
                                                        링킷</a>입니다.<br />
                                                    %s님이 정성스럽게 써주신 이력서를 통해 <br />
                                                    팀 %s에서 <span style="font-weight: 700;">%s</span>에 매칭 요청을 주셨습니다. <br />
                                                    소개글을 확인하고 매칭 요청에 응답해 보세요!
                                                </p>
                                        </td>
                                    </tr>

                                    <!-- Divider -->
                                    <tr>
                                        <td style="background-color: #CBD4E1; height: 1px;"></td>
                                    </tr>

                                    <!-- Profile Section -->
                                    <tr>
                                        <td style="padding: 30px 20px;">
                                            <table width="100%%" cellspacing="0" cellpadding="0" border="0">
                                                <tbody>
                                                    <tr>
                                                        <td style="font-size: 1.875rem; font-weight: bold;">📮</td>
                                                    </tr>
                                                    <tr>
                                                        <td style="font-size: 1.2rem; font-weight: bold; padding-top: 10px;">팀 %s의 소개글</td>
                                                    </tr>
                                                    <tr>
                                                        <td
                                                            style="font-size: 0.875rem; font-weight: 400; line-height: 1.5; color: #000; padding-top: 10px;">
                                                            %s
                                                        </td>
                                                    </tr>
                                                </tbody>
                                            </table>
                                        </td>
                                    </tr>

                                    <!-- Blue Background Section -->
                                    <tr>
                                        <td style="padding: 0 20px;">
                                            <table width="100%%" cellspacing="0" cellpadding="0" border="0"
                                                style="background-color: #F1F4F9; border-radius: 0.5rem; padding: 10px 20px; box-sizing: border-box;">
                                                <tbody>
                                                    <tr>
                                                        <td style="font-size: 0.875rem; font-weight: 600; padding-bottom: 10px;">
                                                            %s팀의 이력
                                                        </td>
                                                    </tr>
                                                    <tr>
                                                        <td
                                                            style="font-size: 0.875rem; font-weight: 400; line-height: 1.5; color: #27364B; padding-bottom: 10px;">
                                                            이력 설명 자리입니다.
                                                        </td>
                                                    </tr>
                                                    <tr>
                                                        <td align="right"
                                                            style="font-size: 0.875rem; font-weight: 600; color: #000;">
                                                            <a href="https://linkit.im" style="text-decoration: none; color: #000;">
                                                                팀 프로필 보러가기
                                                                <svg xmlns="http://www.w3.org/2000/svg" width="5" height="8"
                                                                    viewBox="0 0 5 8" fill="none" style="vertical-align: middle;">
                                                                    <path d="M1 1.02344L4 4.02344L1 7.02344" stroke="black"
                                                                        stroke-width="1.5" stroke-linecap="round"
                                                                        stroke-linejoin="round" />
                                                                </svg>
                                                            </a>
                                                        </td>
                                                    </tr>
                                                </tbody>
                                            </table>
                                        </td>
                                    </tr>

                                    <!-- Response Button -->
                                    <tr>
                                        <td align="center" style="padding: 30px 20px;">
                                            <table width="100%%" cellspacing="0" cellpadding="0" border="0">
                                                <tbody>
                                                    <tr>
                                                        <td align="center"
                                                            style="background-color: #2563EB; border-radius: 0.5rem; height: 55px;">
                                                            <a href="https://linkit.im"
                                                                style="display: block; font-size: 1rem; font-weight: 600; color: #FFF; text-decoration: none; line-height: 55px;">
                                                                %s 팀에게 응답하기 ✍
                                                            </a>
                                                        </td>
                                                    </tr>
                                                </tbody>
                                            </table>
                                        </td>
                                    </tr>

                                    <!-- Footer Section -->
                                    <tr>
                                        <td align="left" style="padding: 30px 20px;">
                                            <img src="https://image-prod.linkit.im/mail/linkit_grey_logo.png" alt="Logo"
                                                style="display: block; width: 92px; height: auto;">
                                        </td>
                                    </tr>
                                    <tr>
                                        <td style="background-color: #CBD4E1; height: 1px;"></td>
                                    </tr>
                                    <tr>
                                        <td align="center"
                                            style="font-size: 0.875rem; font-weight: 400; line-height: 1.5; color: #64748B; padding-top: 20px; padding-bottom: 20px;">
                                            메일링 후 필요한 푸터 내용이 있으면 여기 넣어주세요
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
                receiverName,
                receiverName,
                senderName,
                requestDate.format(DateTimeFormatter.ofPattern("M월 d일")), // Formatting date as "8월 6일"
                senderName,
                requestMessage,
                senderName,
                senderName
        );

        mimeMessage.setContent(msgg, "text/html; charset=utf-8");
        mimeMessage.setFrom(id);

        return mimeMessage;
    }

    // 3.
    private MimeMessage createPrivateToTeamMail(final String receiverEmail, final String receiverName, final String senderName, final LocalDateTime requestDate, final String requestMessage) throws MessagingException {
        final MimeMessage mimeMessage = emailSender.createMimeMessage();

        mimeMessage.addRecipients(Message.RecipientType.TO, receiverEmail);
        mimeMessage.setSubject("[링킷] 팀 소개서 매칭 요청 알림");

        // DateTimeFormatter를 사용하여 날짜를 원하는 형식으로 포맷
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("M월 d일", Locale.KOREAN);
        String formattedDate = requestDate.format(formatter);

        final String msgg = String.format("""
            <table align="center" width="100%%" cellspacing="0" cellpadding="0" border="0"
                style="border-collapse: collapse;">
                <tbody>
                    <tr>
                        <td>
                            <table align="center" width="100%%" cellspacing="0" cellpadding="0" border="0" bgcolor="#ffffff"
                                style="max-width:650px; border-radius: 8px; overflow: hidden; margin: 0 auto;">
                                <tbody>
                                    <!-- Header Section -->
                                    <tr>
                                        <td align="left" style="padding: 20px;">
                                            <img src="https://image-prod.linkit.im/mail/linkit_color_logo.png" alt="Logo"
                                                style="display: block; width: 92px; height: auto;">
                                        </td>
                                    </tr>

                                    <!-- Divider -->
                                    <tr>
                                        <td style="background-color: #CBD4E1; height: 1px;"></td>
                                    </tr>

                                    <!-- Greeting Section -->
                                    <tr>
                                        <td align="center" style="padding: 30px 20px;">
                                            <span style="font-size: 2.5rem; font-weight: 400; color: #000;">👋👋</span>
                                                <p
                                                    style="font-size: 0.875rem; font-weight: 400; line-height: 1.5; color: #000; text-align: center;">
                                                    %s 팀 안녕하세요, <a href="https://linkit.im"
                                                        style="font-weight: 600; color: #2563EB; text-decoration: underline;">
                                                        링킷</a>입니다.<br />
                                                    %s 팀이 정성스럽게 써주신 소개서를 통해 <br />
                                                    %s님께서 <span style="font-weight: 700;">%s</span>에 매칭 요청을 주셨습니다. <br />
                                                    소개글을 확인하고 매칭 요청에 응답해 보세요!
                                                </p>
                                        </td>
                                    </tr>

                                    <!-- Divider -->
                                    <tr>
                                        <td style="background-color: #CBD4E1; height: 1px;"></td>
                                    </tr>

                                    <!-- Profile Section -->
                                    <tr>
                                        <td style="padding: 30px 20px;">
                                            <table width="100%%" cellspacing="0" cellpadding="0" border="0">
                                                <tbody>
                                                    <tr>
                                                        <td style="font-size: 1.875rem; font-weight: bold;">📮</td>
                                                    </tr>
                                                    <tr>
                                                        <td style="font-size: 1.2rem; font-weight: bold; padding-top: 10px;">%s님의
                                                            소개글</td>
                                                    </tr>
                                                    <tr>
                                                        <td
                                                            style="font-size: 0.875rem; font-weight: 400; line-height: 1.5; color: #000; padding-top: 10px;">
                                                            %s
                                                        </td>
                                                    </tr>
                                                </tbody>
                                            </table>
                                        </td>
                                    </tr>

                                    <!-- Blue Background Section -->
                                    <tr>
                                        <td style="padding: 0 20px;">
                                            <table width="100%%" cellspacing="0" cellpadding="0" border="0"
                                                style="background-color: #F1F4F9; border-radius: 0.5rem; padding: 10px 20px; box-sizing: border-box;">
                                                <tbody>
                                                    <tr>
                                                        <td style="font-size: 0.875rem; font-weight: 600; padding-bottom: 10px;">
                                                            %s님의 이력
                                                        </td>
                                                    </tr>
                                                    <tr>
                                                        <td
                                                            style="font-size: 0.875rem; font-weight: 400; line-height: 1.5; color: #27364B; padding-bottom: 10px;">
                                                            이력 설명 자리입니다.
                                                        </td>
                                                    </tr>
                                                    <tr>
                                                        <td align="right"
                                                            style="font-size: 0.875rem; font-weight: 600; color: #000;">
                                                            <a href="https://linkit.im" style="text-decoration: none; color: #000;">
                                                                프로필 보러가기
                                                                <svg xmlns="http://www.w3.org/2000/svg" width="5" height="8"
                                                                    viewBox="0 0 5 8" fill="none" style="vertical-align: middle;">
                                                                    <path d="M1 1.02344L4 4.02344L1 7.02344" stroke="black"
                                                                        stroke-width="1.5" stroke-linecap="round"
                                                                        stroke-linejoin="round" />
                                                                </svg>
                                                            </a>
                                                        </td>
                                                    </tr>
                                                </tbody>
                                            </table>
                                        </td>
                                    </tr>

                                    <!-- Response Button -->
                                    <tr>
                                        <td align="center" style="padding: 30px 20px;">
                                            <table width="100%%" cellspacing="0" cellpadding="0" border="0">
                                                <tbody>
                                                    <tr>
                                                        <td align="center"
                                                            style="background-color: #2563EB; border-radius: 0.5rem; height: 55px;">
                                                            <a href="https://linkit.im"
                                                                style="display: block; font-size: 1rem; font-weight: 600; color: #FFF; text-decoration: none; line-height: 55px;">
                                                                %s님에게 응답하기 ✍
                                                            </a>
                                                        </td>
                                                    </tr>
                                                </tbody>
                                            </table>
                                        </td>
                                    </tr>

                                    <!-- Footer Section -->
                                    <tr>
                                        <td align="left" style="padding: 30px 20px;">
                                            <img src="https://image-prod.linkit.im/mail/linkit_grey_logo.png" alt="Logo"
                                                style="display: block; width: 92px; height: auto;">
                                        </td>
                                    </tr>
                                    <tr>
                                        <td style="background-color: #CBD4E1; height: 1px;"></td>
                                    </tr>
                                    <tr>
                                        <td align="center"
                                            style="font-size: 0.875rem; font-weight: 400; line-height: 1.5; color: #64748B; padding-top: 20px; padding-bottom: 20px;">
                                            메일링 후 필요한 푸터 내용이 있으면 여기 넣어주세요
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
                receiverName,
                receiverName,
                senderName,
                requestDate.format(DateTimeFormatter.ofPattern("M월 d일")), // Formatting date as "8월 6일"
                senderName,
                requestMessage,
                senderName,
                senderName
        );

        mimeMessage.setContent(msgg, "text/html; charset=utf-8");
        mimeMessage.setFrom(id);

        return mimeMessage;
    }

    // 4.
    private MimeMessage createTeamToTeamMail(final String receiverEmail, final String receiverName, final String senderName, final LocalDateTime requestDate, final String requestMessage) throws MessagingException {
        final MimeMessage mimeMessage = emailSender.createMimeMessage();

        mimeMessage.addRecipients(Message.RecipientType.TO, receiverEmail);
        mimeMessage.setSubject("[링킷] 팀 소개서 매칭 요청 알림");

        // DateTimeFormatter를 사용하여 날짜를 원하는 형식으로 포맷
        final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("M월 d일", Locale.KOREAN);
        final String formattedDate = requestDate.format(formatter);

        final String msgg = String.format("""
            <table align="center" width="100%%" cellspacing="0" cellpadding="0" border="0"
                style="border-collapse: collapse;">
                <tbody>
                    <tr>
                        <td>
                            <table align="center" width="100%%" cellspacing="0" cellpadding="0" border="0" bgcolor="#ffffff"
                                style="max-width:650px; border-radius: 8px; overflow: hidden; margin: 0 auto;">
                                <tbody>
                                    <!-- Header Section -->
                                    <tr>
                                        <td align="left" style="padding: 20px;">
                                            <img src="https://image-prod.linkit.im/mail/linkit_color_logo.png" alt="Logo"
                                                style="display: block; width: 92px; height: auto;">
                                        </td>
                                    </tr>

                                    <!-- Divider -->
                                    <tr>
                                        <td style="background-color: #CBD4E1; height: 1px;"></td>
                                    </tr>

                                    <!-- Greeting Section -->
                                    <tr>
                                        <td align="center" style="padding: 30px 20px;">
                                            <span style="font-size: 2.5rem; font-weight: 400; color: #000;">🚀</span>
                                                <p
                                                    style="font-size: 0.875rem; font-weight: 400; line-height: 1.5; color: #000; text-align: center;">
                                                    %s 팀 안녕하세요, <a href="https://linkit.im"
                                                        style="font-weight: 600; color: #2563EB; text-decoration: underline;">
                                                        링킷</a>입니다.<br />
                                                    %s 팀이 정성스럽게 써주신 소개서를 통해 <br />
                                                    팀 %s에서 <span style="font-weight: 700;">%s</span>에 매칭 요청을 주셨습니다. <br />
                                                    소개글을 확인하고 매칭 요청에 응답해 보세요!
                                                </p>
                                        </td>
                                    </tr>

                                    <!-- Divider -->
                                    <tr>
                                        <td style="background-color: #CBD4E1; height: 1px;"></td>
                                    </tr>

                                    <!-- Profile Section -->
                                    <tr>
                                        <td style="padding: 30px 20px;">
                                            <table width="100%%" cellspacing="0" cellpadding="0" border="0">
                                                <tbody>
                                                    <tr>
                                                        <td style="font-size: 1.875rem; font-weight: bold;">📮</td>
                                                    </tr>
                                                    <tr>
                                                        <td style="font-size: 1.2rem; font-weight: bold; padding-top: 10px;">팀 %s의 소개글</td>
                                                    </tr>
                                                    <tr>
                                                        <td
                                                            style="font-size: 0.875rem; font-weight: 400; line-height: 1.5; color: #000; padding-top: 10px;">
                                                            %s
                                                        </td>
                                                    </tr>
                                                </tbody>
                                            </table>
                                        </td>
                                    </tr>

                                    <!-- Blue Background Section -->
                                    <tr>
                                        <td style="padding: 0 20px;">
                                            <table width="100%%" cellspacing="0" cellpadding="0" border="0"
                                                style="background-color: #F1F4F9; border-radius: 0.5rem; padding: 10px 20px; box-sizing: border-box;">
                                                <tbody>
                                                    <tr>
                                                        <td style="font-size: 0.875rem; font-weight: 600; padding-bottom: 10px;">
                                                            %s 팀의 이력
                                                        </td>
                                                    </tr>
                                                    <tr>
                                                        <td
                                                            style="font-size: 0.875rem; font-weight: 400; line-height: 1.5; color: #27364B; padding-bottom: 10px;">
                                                            이력 설명 자리입니다.
                                                        </td>
                                                    </tr>
                                                    <tr>
                                                        <td align="right"
                                                            style="font-size: 0.875rem; font-weight: 600; color: #000;">
                                                            <a href="https://linkit.im" style="text-decoration: none; color: #000;">
                                                                팀 프로필 보러가기
                                                                <svg xmlns="http://www.w3.org/2000/svg" width="5" height="8"
                                                                    viewBox="0 0 5 8" fill="none" style="vertical-align: middle;">
                                                                    <path d="M1 1.02344L4 4.02344L1 7.02344" stroke="black"
                                                                        stroke-width="1.5" stroke-linecap="round"
                                                                        stroke-linejoin="round" />
                                                                </svg>
                                                            </a>
                                                        </td>
                                                    </tr>
                                                </tbody>
                                            </table>
                                        </td>
                                    </tr>

                                    <!-- Response Button -->
                                    <tr>
                                        <td align="center" style="padding: 30px 20px;">
                                            <table width="100%%" cellspacing="0" cellpadding="0" border="0">
                                                <tbody>
                                                    <tr>
                                                        <td align="center"
                                                            style="background-color: #2563EB; border-radius: 0.5rem; height: 55px;">
                                                            <a href="https://linkit.im"
                                                                style="display: block; font-size: 1rem; font-weight: 600; color: #FFF; text-decoration: none; line-height: 55px;">
                                                                %s 팀에게 응답하기 ✍
                                                            </a>
                                                        </td>
                                                    </tr>
                                                </tbody>
                                            </table>
                                        </td>
                                    </tr>

                                    <!-- Footer Section -->
                                    <tr>
                                        <td align="left" style="padding: 30px 20px;">
                                            <img src="https://image-prod.linkit.im/mail/linkit_grey_logo.png" alt="Logo"
                                                style="display: block; width: 92px; height: auto;">
                                        </td>
                                    </tr>
                                    <tr>
                                        <td style="background-color: #CBD4E1; height: 1px;"></td>
                                    </tr>
                                    <tr>
                                        <td align="center"
                                            style="font-size: 0.875rem; font-weight: 400; line-height: 1.5; color: #64748B; padding-top: 20px; padding-bottom: 20px;">
                                            메일링 후 필요한 푸터 내용이 있으면 여기 넣어주세요
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
                receiverName,
                receiverName,
                senderName,
                requestDate.format(DateTimeFormatter.ofPattern("M월 d일")), // Formatting date as "8월 6일"
                senderName,
                requestMessage,
                senderName,
                senderName
        );

        mimeMessage.setContent(msgg, "text/html; charset=utf-8");
        mimeMessage.setFrom(id);

        return mimeMessage;
    }

}
