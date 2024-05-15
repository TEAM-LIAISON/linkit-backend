package liaison.linkit.profile.dto.request.attach;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class AttachUrlCreateRequest {
    @NotNull(message = "첨부 URL을 입력해주세요.")
    private final String attachUrl;
}
