package liaison.linkit.member.business;

import liaison.linkit.common.annotation.Mapper;
import liaison.linkit.member.domain.Member;
import liaison.linkit.member.domain.MemberBasicInform;
import liaison.linkit.member.presentation.dto.request.memberBasicInform.MemberBasicInformRequestDTO.UpdateMemberBasicInformRequest;
import liaison.linkit.member.presentation.dto.response.MemberBasicInformResponseDTO;

@Mapper
public class MemberBasicInformMapper {

    // request -> 회원 기본 정보 객체로 전환
    public MemberBasicInform toMemberBasicInform(final Member member, final UpdateMemberBasicInformRequest request) {
        return MemberBasicInform.builder()
                .member(member)
                .memberName(request.getMemberName())
                .contact(request.getContact())
                .serviceUseAgree(true)
                .privateInformAgree(true)
                .ageCheck(true)
                .build();
    }

    public MemberBasicInformResponseDTO.MemberBasicInformDetail toMemberBasicInformDetail(
            final MemberBasicInform memberBasicInform,
            final String email
    ) {
        return MemberBasicInformResponseDTO.MemberBasicInformDetail.builder()
                .memberBasicInformId(memberBasicInform.getId())
                .memberName(memberBasicInform.getMemberName())
                .contact(memberBasicInform.getContact())
                .email(email)
                .build();
    }

    public MemberBasicInformResponseDTO.UpdateMemberBasicInformResponse toMemberBasicInformResponse(
            final MemberBasicInform memberBasicInform,
            final String email
    ) {
        return MemberBasicInformResponseDTO.UpdateMemberBasicInformResponse.builder()
                .memberBasicInformId(memberBasicInform.getId())
                .memberName(memberBasicInform.getMemberName())
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
                .build();
    }
}
