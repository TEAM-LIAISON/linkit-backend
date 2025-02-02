package liaison.linkit.member.business;

import java.time.LocalDateTime;
import liaison.linkit.common.annotation.Mapper;
import liaison.linkit.member.domain.MemberBasicInform;
import liaison.linkit.member.domain.type.Platform;
import liaison.linkit.member.presentation.dto.MemberBasicInformResponseDTO;
import liaison.linkit.member.presentation.dto.MemberBasicInformResponseDTO.MailReAuthenticationResponse;
import liaison.linkit.member.presentation.dto.MemberBasicInformResponseDTO.MailVerificationResponse;

@Mapper
public class MemberBasicInformMapper {

    // 회원 기본 정보 조회 DTO
    public MemberBasicInformResponseDTO.MemberBasicInformDetail toMemberBasicInformDetail(
            final MemberBasicInform memberBasicInform,
            final String email,
            final String emailId,
            final Platform platform
    ) {
        return MemberBasicInformResponseDTO.MemberBasicInformDetail.builder()
                .memberBasicInformId(memberBasicInform.getId())
                .memberName(memberBasicInform.getMemberName())
                .contact(memberBasicInform.getContact())
                .email(email)
                .emailId(emailId)
                .isServiceUseAgree(memberBasicInform.isServiceUseAgree())
                .isPrivateInformAgree(memberBasicInform.isPrivateInformAgree())
                .isAgeCheck(memberBasicInform.isAgeCheck())
                .isMarketingAgree(memberBasicInform.isMarketingAgree())
                .platform(platform)
                .build();
    }

    public MemberBasicInformResponseDTO.UpdateMemberBasicInformResponse toMemberBasicInformResponse(
            final MemberBasicInform memberBasicInform,
            final String email,
            final String emailId
    ) {
        return MemberBasicInformResponseDTO.UpdateMemberBasicInformResponse.builder()
                .memberBasicInformId(memberBasicInform.getId())
                .memberName(memberBasicInform.getMemberName())
                .emailId(emailId)
                .contact(memberBasicInform.getContact())
                .email(email)
                .build();
    }

    public MemberBasicInformResponseDTO.UpdateConsentServiceUseResponse toUpdateConsentServiceUseResponse(
            final MemberBasicInform memberBasicInform
    ) {
        return MemberBasicInformResponseDTO.UpdateConsentServiceUseResponse.builder()
                .memberBasicInformId(memberBasicInform.getId())
                .isServiceUseAgree(memberBasicInform.isServiceUseAgree())
                .isPrivateInformAgree(memberBasicInform.isPrivateInformAgree())
                .isAgeCheck(memberBasicInform.isAgeCheck())
                .isMarketingAgree(memberBasicInform.isMarketingAgree())
                .emailId(memberBasicInform.getMember().getEmailId())
                .memberName(memberBasicInform.getMemberName())
                .build();
    }

    public MemberBasicInformResponseDTO.UpdateMemberNameResponse toUpdateMemberNameResponse(
            final MemberBasicInform memberBasicInform
    ) {
        return MemberBasicInformResponseDTO.UpdateMemberNameResponse.builder()
                .memberName(memberBasicInform.getMemberName())
                .build();
    }

    public MemberBasicInformResponseDTO.UpdateMemberContactResponse toUpdateMemberContactResponse(
            final MemberBasicInform memberBasicInform
    ) {
        return MemberBasicInformResponseDTO.UpdateMemberContactResponse.builder()
                .contact(memberBasicInform.getContact())
                .build();
    }

    public MemberBasicInformResponseDTO.UpdateConsentMarketingResponse toUpdateConsentMarketingResponse(
            final MemberBasicInform memberBasicInform
    ) {
        return MemberBasicInformResponseDTO.UpdateConsentMarketingResponse.builder()
                .isMarketingAgree(memberBasicInform.isMarketingAgree()).build();
    }

    public MailReAuthenticationResponse toReAuthenticationResponse() {
        return MailReAuthenticationResponse.builder()
                .reAuthenticationEmailSendAt(LocalDateTime.now())
                .build();
    }

    public MailVerificationResponse toEmailVerificationResponse(final String email) {
        return MailVerificationResponse
                .builder()
                .changedEmail(email)
                .verificationSuccessAt(LocalDateTime.now())
                .build();
    }
}
