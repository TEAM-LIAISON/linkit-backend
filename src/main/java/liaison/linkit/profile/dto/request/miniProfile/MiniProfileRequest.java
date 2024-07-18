package liaison.linkit.profile.dto.request.miniProfile;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class MiniProfileRequest {

    @NotNull(message = "프로필 제목을 입력해주세요")
    // 나의 프로필 제목
    private final String profileTitle;

    @NotNull(message = "나를 소개하는 키워드를 알려주세요")
    private final List<String> myKeywordNames;

    @NotNull(message = "프로필 활성화 여부를 선택해주세요")
    private final Boolean isActivate;
}
