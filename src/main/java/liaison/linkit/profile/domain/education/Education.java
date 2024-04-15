package liaison.linkit.profile.domain.education;

import jakarta.persistence.*;
import liaison.linkit.profile.domain.Profile;
import liaison.linkit.profile.dto.request.EducationUpdateRequest;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import static jakarta.persistence.FetchType.LAZY;
import static jakarta.persistence.GenerationType.IDENTITY;
import static lombok.AccessLevel.PROTECTED;

@Entity
@Getter
@AllArgsConstructor
@NoArgsConstructor(access = PROTECTED)
public class Education {
    @Id
    @GeneratedValue(strategy = IDENTITY)
    @Column(name = "education_id")
    private Long id;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "profile_id")
    private Profile profile;

    @Column(nullable = false)
    private int admissionYear;

    @Column(nullable = false)
    private int admissionMonth;

    @Column(nullable = false)
    private int graduationYear;

    @Column(nullable = false)
    private int graduationMonth;

    @Column(nullable = false)
    private String educationDescription;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "school_id")
    private School school;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "degree_id")
    private Degree degree;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "major_id")
    private Major major;

    public static Education of(
            final Profile profile,
            final int admissionYear,
            final  int admissionMonth,
            final int graduationYear,
            final int graduationMonth,
            final String educationDescription,
            final School school,
            final Degree degree,
            final Major major
    ){
        return new Education(
                null,
                profile,
                admissionYear,
                admissionMonth,
                graduationYear,
                graduationMonth,
                educationDescription,
                school,
                degree,
                major
        );
    }

    public void update(final EducationUpdateRequest educationUpdateRequest) {
        this.admissionYear = educationUpdateRequest.getAdmissionYear();
        this.admissionMonth = educationUpdateRequest.getGraduationMonth();
        this.graduationYear = educationUpdateRequest.getGraduationYear();
        this.graduationMonth = educationUpdateRequest.getGraduationMonth();
        this.educationDescription = educationUpdateRequest.getEducationDescription();
        this.school = educationUpdateRequest.getSchool();
        this.degree = educationUpdateRequest.getDegree();
    }
}
