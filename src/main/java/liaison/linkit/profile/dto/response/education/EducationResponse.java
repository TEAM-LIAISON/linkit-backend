package liaison.linkit.profile.dto.response.education;

import liaison.linkit.profile.domain.education.Education;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class EducationResponse {

    private final Long id;
    private final int admissionYear;
    private final int graduationYear;

    // 학교명
    private final String universityName;
    // 전공명
    private final String majorName;
    // 재학 기간 관련 타입
    private final String degreeName;

    public static EducationResponse of(final Education education) {
        return new EducationResponse(
                education.getId(),
                education.getAdmissionYear(),
                education.getGraduationYear(),
                education.getUniversityName(),
                education.getMajorName(),
                education.getDegree().getDegreeName()
        );
    }

    public static EducationResponse personalEducation(final Education education) {
        return new EducationResponse(
                education.getId(),
                education.getAdmissionYear(),
                education.getGraduationYear(),
                education.getUniversityName(),
                education.getMajorName(),
                education.getDegree().getDegreeName()
        );
    }
}
