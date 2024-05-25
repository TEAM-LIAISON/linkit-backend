package liaison.linkit.profile.dto.response;

import liaison.linkit.profile.domain.education.Degree;
import liaison.linkit.profile.domain.education.Education;
import liaison.linkit.profile.domain.education.Major;
import liaison.linkit.profile.domain.education.University;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import static lombok.AccessLevel.PRIVATE;

@Getter
@RequiredArgsConstructor(access = PRIVATE)
public class EducationResponse {

    private final Long id;
    private final int admissionYear;
    private final int graduationYear;

    private final University university;
    private final Degree degree;
    private final Major major;

    public static EducationResponse of(final Education education) {
        return new EducationResponse(
                education.getId(),
                education.getAdmissionYear(),
                education.getGraduationYear(),
                education.getUniversity(),
                education.getDegree(),
                education.getMajor()
        );
    }

    public static EducationResponse personalEducation(final Education education) {
        return new EducationResponse(
                education.getId(),
                education.getAdmissionYear(),
                education.getGraduationYear(),
                education.getUniversity(),
                education.getDegree(),
                education.getMajor()
        );
    }
}
