package liaison.linkit.profile.dto.request.Attach;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class AttachFileUpdateRequest {
    @NotNull(message = "첨부 파일을 업로드해주세요.")
    private final String attachFile;
}
