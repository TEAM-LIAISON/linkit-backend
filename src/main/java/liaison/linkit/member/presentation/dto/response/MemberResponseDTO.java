package liaison.linkit.member.presentation.dto.response;

import liaison.linkit.member.domain.Member;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class MemberResponseDTO {
    private final String email;

    public static MemberResponseDTO getEmail(final Member member) {
        return new MemberResponseDTO(
                member.getEmail()
        );
    }

}
