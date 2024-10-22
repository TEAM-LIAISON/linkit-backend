package liaison.linkit.member.business;

import liaison.linkit.common.annotation.Mapper;
import liaison.linkit.member.domain.Member;
import liaison.linkit.member.domain.MemberBasicInform;
import liaison.linkit.member.presentation.dto.request.memberBasicInform.MemberBasicInformRequestDTO.memberBasicInformRequest;
import liaison.linkit.member.presentation.dto.response.MemberBasicInformResponseDTO;
import liaison.linkit.member.presentation.dto.response.MemberBasicInformResponseDTO.RequestMemberBasicInform;

@Mapper
public class MemberBasicInformMapper {

    // request -> 회원 기본 정보 객체로 전환
    public MemberBasicInform toMemberBasicInform(final Member member, final memberBasicInformRequest request) {
        return MemberBasicInform.builder()
                .member(member)
                .memberName(request.getMemberName())
                .contact(request.getContact())
                .serviceUseAgree(true)
                .privateInformAgree(true)
                .ageCheck(true)
                .marketingAgree(request.isMarketingAgree())
                .build();
    }

    public RequestMemberBasicInform toRequestMemberBasicInform(final MemberBasicInform memberBasicInform) {
        return RequestMemberBasicInform.builder()
                .memberName(memberBasicInform.getMemberName())
                .contact(memberBasicInform.getContact())
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
                .marketingAgree(memberBasicInform.isMarketingAgree())
                .build();
    }
}
