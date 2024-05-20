package liaison.linkit.profile.dto.request.miniProfile;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDate;

@Getter
@AllArgsConstructor
public class MiniProfileCreateRequest {

    // 나의 프로필 제목
    private final String profileTitle;

    // 프로필 업로드 기간
    private final LocalDate uploadPeriod;

    // 마감 선택 여부
    private final boolean uploadDeadline;

    // 협업 시 중요한 나의 가치
    private final String myValue;

    // 나의 스킬셋
    private final String skillSets;

}
