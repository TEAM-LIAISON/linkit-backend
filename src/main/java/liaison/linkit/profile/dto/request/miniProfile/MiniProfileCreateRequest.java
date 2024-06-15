package liaison.linkit.profile.dto.request.miniProfile;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDate;

@Getter
@AllArgsConstructor
public class MiniProfileCreateRequest {

    @NotNull(message = "프로필 제목을 입력해주세요")
    // 나의 프로필 제목
    private final String profileTitle;

    @NotNull(message = "프로필 업로드 기간을 설정해주세요")
    // 프로필 업로드 기간
    private final LocalDate uploadPeriod;

    @NotNull(message = "프로필 마감 여부를 선택해주세요")
    // 마감 선택 여부
    private final boolean uploadDeadline;

    @NotNull(message = "협업 시 중요한 나의 가치를 알려주세요")
    // 협업 시 중요한 나의 가치
    private final String myValue;

    @NotNull(message = "나의 스킬셋을 알려주세요")
    // 나의 스킬셋
    private final String skillSets;
}
