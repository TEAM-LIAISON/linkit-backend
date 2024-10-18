package liaison.linkit.profile.dto.response.education;

import liaison.linkit.profile.domain.ProfileEducation;
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

    public static EducationResponse of(final ProfileEducation profileEducation) {
        return new EducationResponse(
                profileEducation.getId(),
                profileEducation.getAdmissionYear(),
                profileEducation.getGraduationYear(),
                profileEducation.getUniversityName(),
                profileEducation.getMajorName(),
                profileEducation.getDegree().getDegreeName()
        );
    }

    public static EducationResponse personalEducation(final ProfileEducation profileEducation) {
        return new EducationResponse(
                profileEducation.getId(),
                profileEducation.getAdmissionYear(),
                profileEducation.getGraduationYear(),
                profileEducation.getUniversityName(),
                profileEducation.getMajorName(),
                profileEducation.getDegree().getDegreeName()
        );
    }
}
