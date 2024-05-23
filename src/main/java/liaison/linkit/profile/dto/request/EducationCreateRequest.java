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
    private final int admissionMonth;
    private final int graduationYear;
    private final int graduationMonth;
    private final String educationDescription;

    private final String universityName;
    private final String degreeName;
    private final String majorName;

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
                admissionMonth,
                graduationYear,
                graduationMonth,
                educationDescription,
                university,
                degree,
                major
        );
    }
}
