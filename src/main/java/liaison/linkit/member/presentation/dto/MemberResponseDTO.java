package liaison.linkit.member.presentation.dto;

import liaison.linkit.member.domain.Member;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class MemberResponseDTO {
    private final String email;

    public static MemberResponseDTO getEmail(final Member member) {
        return new MemberResponseDTO(member.getEmail());
    }

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UpdateMemberUserIdResponse {
        private String emailId;
    }
}
