package liaison.linkit.profile.dto.request;

import liaison.linkit.profile.domain.Profile;
import liaison.linkit.profile.domain.education.Degree;
import liaison.linkit.profile.domain.education.Education;
import liaison.linkit.profile.domain.education.Major;
import liaison.linkit.profile.domain.education.University;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class EducationCreateRequest {

    private final int admissionYear;
    private final int graduationYear;

    // 학교명
    private final String universityName;
    // 전공명
    private final String majorName;
    // 재학 기간 관련 타입
    private final String degreeName;

    public Education toEntity(
            final Profile profile,
            final University university,
            final Degree degree,
            final Major major
    ) {
        return new Education(
                null,
                profile,
                admissionYear,
                graduationYear,
                university,
                degree,
                major
        );
    }
}
