package liaison.linkit.profile.dto.request;

import liaison.linkit.profile.domain.education.Degree;
import liaison.linkit.profile.domain.education.Major;
import liaison.linkit.profile.domain.education.School;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class EducationUpdateRequest {
    private final int admissionYear;
    private final int admissionMonth;
    private final int graduationYear;
    private final int graduationMonth;
    private final String educationDescription;

    private final School school;
    private final Degree degree;
    private final Major major;
}
