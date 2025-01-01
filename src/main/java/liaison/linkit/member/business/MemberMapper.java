package liaison.linkit.member.business;

import liaison.linkit.common.annotation.Mapper;
import liaison.linkit.member.domain.Member;
import liaison.linkit.member.presentation.dto.response.MemberResponseDTO;
import liaison.linkit.member.presentation.dto.response.MemberResponseDTO.UpdateMemberUserIdResponse;

@Mapper
public class MemberMapper {
    public MemberResponseDTO.UpdateMemberUserIdResponse toUpdateUserIdResponse(
            final Member member
    ) {
        return UpdateMemberUserIdResponse.builder()
                .emailId(member.getEmailId())
                .build();
    }
    
}
