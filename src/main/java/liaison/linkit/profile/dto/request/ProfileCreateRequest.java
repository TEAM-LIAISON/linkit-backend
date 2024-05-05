package liaison.linkit.profile.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(force = true)
public class ProfileCreateRequest {
    @NotNull(message = "자기소개를 반드시 입력해주세요.")
    private final String introduction;
    public ProfileCreateRequest(final String introduction) {
        this.introduction = introduction;
    }
}
