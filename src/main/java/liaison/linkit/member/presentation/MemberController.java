package liaison.linkit.member.presentation;

import liaison.linkit.auth.Auth;
import liaison.linkit.auth.MemberOnly;
import liaison.linkit.auth.domain.Accessor;
import liaison.linkit.common.presentation.CommonResponse;
import liaison.linkit.member.business.MemberService;
import liaison.linkit.member.presentation.dto.MemberBasicInformRequestDTO;
import liaison.linkit.member.presentation.dto.MemberBasicInformRequestDTO.AuthCodeVerificationRequest;
import liaison.linkit.member.presentation.dto.MemberBasicInformRequestDTO.MailReAuthenticationRequest;
import liaison.linkit.member.presentation.dto.MemberBasicInformRequestDTO.UpdateConsentMarketingRequest;
import liaison.linkit.member.presentation.dto.MemberRequestDTO;
import liaison.linkit.member.presentation.dto.MemberBasicInformResponseDTO;
import liaison.linkit.member.presentation.dto.MemberBasicInformResponseDTO.UpdateConsentMarketingResponse;
import liaison.linkit.member.presentation.dto.MemberResponseDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/member")
@Slf4j
public class MemberController {

    public final MemberService memberService;

    // [0.1.2] 기본 정보 01
    @PostMapping("/basic-inform")
    @MemberOnly
    public CommonResponse<MemberBasicInformResponseDTO.UpdateMemberBasicInformResponse> updateMemberBasicInform(
            @Auth final Accessor accessor,
            @RequestBody final MemberBasicInformRequestDTO.UpdateMemberBasicInformRequest request
    ) {
        log.info("memberId = {}의 회원 기본 정보 수정 요청 발생", accessor.getMemberId());
        return CommonResponse.onSuccess(memberService.updateMemberBasicInform(accessor.getMemberId(), request));
    }

    // 회원 유저 아이디 수정
    @PostMapping("/userId")
    @MemberOnly
    public CommonResponse<MemberResponseDTO.UpdateMemberUserIdResponse> updateMemberUserId(
            @Auth final Accessor accessor,
            @RequestBody final MemberRequestDTO.UpdateMemberUserIdRequest updateMemberUserIdRequest
    ) {
        return CommonResponse.onSuccess(memberService.updateMemberUserId(accessor.getMemberId(), updateMemberUserIdRequest));
    }

    // 서비스 이용 동의 수정
    @PostMapping("/consent")
    @MemberOnly
    public CommonResponse<MemberBasicInformResponseDTO.UpdateConsentServiceUseResponse> updateConsentServiceUse(
            @Auth final Accessor accessor,
            @RequestBody final MemberBasicInformRequestDTO.UpdateConsentServiceUseRequest updateConsentServiceUseRequest
    ) {
        log.info("memberId = {}의 서비스 이용 동의 수정 요청 발생", accessor.getMemberId());
        return CommonResponse.onSuccess(memberService.updateConsentServiceUse(accessor.getMemberId(), updateConsentServiceUseRequest));
    }

    // 회원 기본 정보 조회
    @GetMapping("/basic-inform")
    @MemberOnly
    public CommonResponse<MemberBasicInformResponseDTO.MemberBasicInformDetail> getMemberBasicInform(
            @Auth final Accessor accessor
    ) {
        log.info("memberId = {}의 회원 기본 정보 조회 요청 발생", accessor.getMemberId());
        return CommonResponse.onSuccess(memberService.getMemberBasicInform(accessor.getMemberId()));
    }

    // 회원 이름 정보 수정
    @PostMapping("/name")
    @MemberOnly
    public CommonResponse<MemberBasicInformResponseDTO.UpdateMemberNameResponse> updateMemberName(
            @Auth final Accessor accessor,
            @RequestBody final MemberBasicInformRequestDTO.UpdateMemberNameRequest updateMemberNameRequest
    ) {
        log.info("memberId = {}의 회원 이름 정보 수정 요청 발생", accessor.getMemberId());
        return CommonResponse.onSuccess(memberService.updateMemberName(accessor.getMemberId(), updateMemberNameRequest));
    }

    // 회원 연락처 정보 수정
    @PostMapping("/contact")
    @MemberOnly
    public CommonResponse<MemberBasicInformResponseDTO.UpdateMemberContactResponse> updateMemberContact(
            @Auth final Accessor accessor,
            @RequestBody final MemberBasicInformRequestDTO.UpdateMemberContactRequest updateMemberContactRequest
    ) {
        log.info("memberId = {}의 회원 연락처 정보 수정 요청 발생", accessor.getMemberId());
        return CommonResponse.onSuccess(memberService.updateMemberContact(accessor.getMemberId(), updateMemberContactRequest));
    }

    // 회원 광고성 정보 수신 동의 수정
    @PostMapping("/consent/marketing")
    @MemberOnly
    public CommonResponse<UpdateConsentMarketingResponse> updateConsentMarketing(
            @Auth final Accessor accessor,
            @RequestBody final UpdateConsentMarketingRequest updateConsentMarketingRequest
    ) {
        log.info("memberId = {}의 회원 마케팅 수신 동의 정보 수정 요청 발생", accessor.getMemberId());
        return CommonResponse.onSuccess(memberService.updateConsentMarketing(accessor.getMemberId(), updateConsentMarketingRequest));
    }

    // 회원이 이메일 재인증을 한다
    @PostMapping("/email/re-authentication")
    @MemberOnly
    public CommonResponse<MemberBasicInformResponseDTO.MailReAuthenticationResponse> reAuthenticationEmail(
            @Auth final Accessor accessor,
            @RequestBody final MailReAuthenticationRequest emailReAuthenticationRequest
    ) throws Exception {
        return CommonResponse.onSuccess(memberService.reAuthenticationEmail(accessor.getMemberId(), emailReAuthenticationRequest));
    }

    // 회원이 수신한 재인증 코드를 입력한다.
    @PostMapping("/email/verification")
    @MemberOnly
    public CommonResponse<MemberBasicInformResponseDTO.MailVerificationResponse> verificationAuthCode(
            @Auth final Accessor accessor,
            @RequestBody final AuthCodeVerificationRequest authCodeVerificationRequest
    ) {
        return CommonResponse.onSuccess(memberService.verifyAuthCodeAndChangeAccountEmail(accessor.getMemberId(), authCodeVerificationRequest));
    }
}

