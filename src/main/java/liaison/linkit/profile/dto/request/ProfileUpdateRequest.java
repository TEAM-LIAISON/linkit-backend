package liaison.linkit.profile.dto.request;

import jakarta.validation.constraints.NotNull;
import liaison.linkit.member.domain.Member;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ProfileUpdateRequest {

    private final Member member;

    @NotNull(message = "자기소개를 반드시 입력해주세요.")
    private final String introduction;
}
