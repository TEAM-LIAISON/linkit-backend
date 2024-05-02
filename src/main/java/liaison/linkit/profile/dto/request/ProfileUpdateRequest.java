package liaison.linkit.profile.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ProfileUpdateRequest {

    @NotNull(message = "자기소개를 입력해주세요")
    private final String introduction;
}
