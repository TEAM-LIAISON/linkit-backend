package liaison.linkit.profile.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class EducationUpdateRequest {
    private final int admissionYear;
    private final int graduationYear;

    // 학교명
    private final String universityName;
    // 전공명
    private final String majorName;
    // 재학 기간 관련 타입
    private final String degreeName;
}
