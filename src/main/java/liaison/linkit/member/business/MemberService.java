package liaison.linkit.member.business;

import jakarta.mail.MessagingException;
import java.util.Random;
import liaison.linkit.login.exception.AuthCodeBadRequestException;
import liaison.linkit.login.infrastructure.MailReAuthenticationRedisUtil;
import liaison.linkit.mail.service.AuthCodeMailService;
import liaison.linkit.member.domain.Member;
import liaison.linkit.member.domain.MemberBasicInform;
import liaison.linkit.member.exception.member.DuplicateEmailIdException;
import liaison.linkit.member.implement.MemberBasicInformCommandAdapter;
import liaison.linkit.member.implement.MemberBasicInformQueryAdapter;
import liaison.linkit.member.implement.MemberCommandAdapter;
import liaison.linkit.member.implement.MemberQueryAdapter;
import liaison.linkit.member.presentation.dto.MemberBasicInformRequestDTO.AuthCodeVerificationRequest;
import liaison.linkit.member.presentation.dto.MemberBasicInformRequestDTO.MailReAuthenticationRequest;
import liaison.linkit.member.presentation.dto.MemberBasicInformRequestDTO.UpdateConsentMarketingRequest;
import liaison.linkit.member.presentation.dto.MemberBasicInformRequestDTO.UpdateConsentServiceUseRequest;
import liaison.linkit.member.presentation.dto.MemberBasicInformRequestDTO.UpdateMemberBasicInformRequest;
import liaison.linkit.member.presentation.dto.MemberBasicInformRequestDTO.UpdateMemberContactRequest;
import liaison.linkit.member.presentation.dto.MemberBasicInformRequestDTO.UpdateMemberNameRequest;
import liaison.linkit.member.presentation.dto.MemberBasicInformResponseDTO;
import liaison.linkit.member.presentation.dto.MemberBasicInformResponseDTO.MailReAuthenticationResponse;
import liaison.linkit.member.presentation.dto.MemberBasicInformResponseDTO.MailVerificationResponse;
import liaison.linkit.member.presentation.dto.MemberBasicInformResponseDTO.UpdateConsentMarketingResponse;
import liaison.linkit.member.presentation.dto.MemberRequestDTO;
import liaison.linkit.member.presentation.dto.MemberResponseDTO;
import liaison.linkit.notification.business.NotificationMapper;
import liaison.linkit.notification.domain.type.NotificationType;
import liaison.linkit.notification.domain.type.SubNotificationType;
import liaison.linkit.notification.presentation.dto.NotificationResponseDTO.NotificationDetails;
import liaison.linkit.notification.service.HeaderNotificationService;
import liaison.linkit.notification.service.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

// 소셜로그인 이후 기본 정보 기입 플로우부터
@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class MemberService {

    private final MemberQueryAdapter memberQueryAdapter;
    private final MemberBasicInformQueryAdapter memberBasicInformQueryAdapter;
    private final MemberBasicInformCommandAdapter memberBasicInformCommandAdapter;
    private final MemberBasicInformMapper memberBasicInformMapper;

    private final MailReAuthenticationRedisUtil mailReAuthenticationRedisUtil;
    private final AuthCodeMailService authCodeMailService;
    private final MemberCommandAdapter memberCommandAdapter;
    private final MemberMapper memberMapper;
    private final NotificationService notificationService;
    private final NotificationMapper notificationMapper;
    private final HeaderNotificationService headerNotificationService;

    // 회원 기본 정보 요청 (UPDATE)
    public MemberBasicInformResponseDTO.UpdateMemberBasicInformResponse updateMemberBasicInform(final Long memberId, final UpdateMemberBasicInformRequest request) {

        if (memberQueryAdapter.existsByEmailId(request.getEmailId())) {
            throw DuplicateEmailIdException.EXCEPTION;
        }

        final MemberBasicInform updatedMemberBasicInform = memberBasicInformCommandAdapter.updateMemberBasicInform(memberId, request);

        final Member updatedMember = memberCommandAdapter.updateEmailId(memberId, request.getEmailId());

        updatedMember.setCreateMemberBasicInform(updatedMemberBasicInform.isMemberBasicInform());

        // 회원 가입 시 시스템 알림 발송
        NotificationDetails welcomeLinkitNotificationDetails = NotificationDetails.welcomeLinkit(
                updatedMember.getEmailId(),
                "프로필을 완성하러 가볼까요?"
        );

        notificationService.alertNewNotification(
                notificationMapper.toNotification(
                        updatedMember.getId(),
                        NotificationType.SYSTEM,
                        SubNotificationType.WELCOME_LINKIT,
                        welcomeLinkitNotificationDetails
                )
        );

        headerNotificationService.publishNotificationCount(updatedMember.getId());

        return memberBasicInformMapper.toMemberBasicInformResponse(updatedMemberBasicInform, updatedMember.getEmail(), updatedMember.getEmailId());
    }

    // 서비스 이용 동의 요청 (UPDATE)
    public MemberBasicInformResponseDTO.UpdateConsentServiceUseResponse updateConsentServiceUse(final Long memberId, final UpdateConsentServiceUseRequest request) {
        final MemberBasicInform updatedMemberBasicInform = memberBasicInformCommandAdapter.updateConsentServiceUse(memberId, request);
        return memberBasicInformMapper.toUpdateConsentServiceUseResponse(updatedMemberBasicInform);
    }

    // 회원 기본 정보 조회 (READ)
    public MemberBasicInformResponseDTO.MemberBasicInformDetail getMemberBasicInform(final Long memberId) {

        final MemberBasicInform memberBasicInform = memberBasicInformQueryAdapter.findByMemberId(memberId);
        final String email = memberQueryAdapter.findEmailById(memberId);
        final String emailId = memberQueryAdapter.findEmailIdById(memberId);

        return memberBasicInformMapper.toMemberBasicInformDetail(memberBasicInform, email, emailId, memberBasicInform.getMember().getPlatform());
    }


    // 회원 이름 수정 요청 (UPDATE)
    public MemberBasicInformResponseDTO.UpdateMemberNameResponse updateMemberName(final Long memberId, final UpdateMemberNameRequest updateMemberNameRequest) {
        final MemberBasicInform updatedMemberBasicInform = memberBasicInformCommandAdapter.updateMemberName(memberId, updateMemberNameRequest);
        return memberBasicInformMapper.toUpdateMemberNameResponse(updatedMemberBasicInform);
    }

    // 회원 유저 아이디 수정 요쳥 (UPDATE)
    public MemberResponseDTO.UpdateMemberUserIdResponse updateMemberUserId(final Long memberId, final MemberRequestDTO.UpdateMemberUserIdRequest updateMemberUserIdRequest) {
        final Member updatedMember = memberCommandAdapter.updateEmailId(memberId, updateMemberUserIdRequest.getEmailId());
        return memberMapper.toUpdateUserIdResponse(updatedMember);
    }

    // 회원 전화번호 수정 요청 (UPDATE)
    public MemberBasicInformResponseDTO.UpdateMemberContactResponse updateMemberContact(final Long memberId, final UpdateMemberContactRequest updateMemberContactRequest) {
        final MemberBasicInform updateMemberBasicInform = memberBasicInformCommandAdapter.updateMemberContact(memberId, updateMemberContactRequest);
        return memberBasicInformMapper.toUpdateMemberContactResponse(updateMemberBasicInform);
    }

    // 회원 마케팅 수신 동의 수정 요청 (UPDATE)
    public UpdateConsentMarketingResponse updateConsentMarketing(final Long memberId, final UpdateConsentMarketingRequest updateConsentMarketingRequest) {
        final MemberBasicInform updatedMemberBasicInform = memberBasicInformCommandAdapter.updateConsentMarketing(memberId, updateConsentMarketingRequest);
        return memberBasicInformMapper.toUpdateConsentMarketingResponse(updatedMemberBasicInform);
    }


    public MailReAuthenticationResponse reAuthenticationEmail(
            final Long memberId,
            final MailReAuthenticationRequest mailReAuthenticationRequest
    ) throws MessagingException {

        // 레디스에서 이메일 해시키가 존재한다면 데이터를 삭제한다. (5분 만료 이전에 다시 요청 보내는 경우 대비)
        if (mailReAuthenticationRedisUtil.existData(mailReAuthenticationRequest.getEmail())) {
            mailReAuthenticationRedisUtil.deleteData(mailReAuthenticationRequest.getEmail());
        }

        // 재인증 코드를 생성한다
        final String authCode = createCode();

        // Redis 에 해당 인증코드 인증 시간 설정 (10분)
        mailReAuthenticationRedisUtil.setDataExpire(mailReAuthenticationRequest.getEmail(), authCode, 18 * 10L);

        // DB 조회
        final Member member = memberQueryAdapter.findById(memberId);

        // 사용자가 입력한 이메일에 재인증 코드를 발송한다.
        authCodeMailService.sendMailReAuthenticationCode(member.getMemberBasicInform().getMemberName(), mailReAuthenticationRequest.getEmail(), authCode);

        // 재인증 코드를 발송한 시간 발행
        return memberBasicInformMapper.toReAuthenticationResponse();
    }

    private String createCode() {
        int leftLimit = 48; // number '0'
        int rightLimit = 57; // number '9'
        int targetStringLength = 6;
        Random random = new Random();

        return random.ints(leftLimit, rightLimit + 1)
                .limit(targetStringLength)
                .collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append)
                .toString();
    }

    public MailVerificationResponse verifyAuthCodeAndChangeAccountEmail(final Long memberId, final AuthCodeVerificationRequest authCodeVerificationRequest) {
        final String authCode = authCodeVerificationRequest.getAuthCode();
        log.info("authCode = {}", authCode);
        final String changeRequestEmail = authCodeVerificationRequest.getChangeRequestEmail();
        log.info("changeRequestEmail = {}", changeRequestEmail);

        // 인증 코드가 잘못 입력된 경우
        if (!verifyEmailCode(changeRequestEmail, authCode)) {
            throw AuthCodeBadRequestException.EXCEPTION;
        }

        final Member member = memberQueryAdapter.findById(memberId);
        member.updateEmail(changeRequestEmail);

        return memberBasicInformMapper.toEmailVerificationResponse(changeRequestEmail);
    }

    // 코드 검증
    public Boolean verifyEmailCode(String email, String code) {
        String codeFoundByEmail = mailReAuthenticationRedisUtil.getData(email);
        log.info("codeFoundByEmail = {}", codeFoundByEmail);
        if (codeFoundByEmail == null) {
            log.info("false 실행");
            return false;
        }
        return codeFoundByEmail.equals(code);
    }
}
