package liaison.linkit.profile.domain.education;

import jakarta.persistence.*;
import liaison.linkit.profile.domain.Profile;
import liaison.linkit.profile.dto.request.education.EducationCreateRequest;
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
    private int graduationYear;

    @Column(nullable = false)
    private String universityName;

    @Column(nullable = false)
    private String majorName;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "degree_id")
    private Degree degree;

    public static Education of(
            final Profile profile,
            final int admissionYear,
            final int graduationYear,
            final String universityName,
            final String majorName,
            final Degree degree
    ){
        return new Education(
                null,
                profile,
                admissionYear,
                graduationYear,
                universityName,
                majorName,
                degree
        );
    }

    public void update(
            final EducationCreateRequest educationCreateRequest,
            final String universityName,
            final String majorName,
            final Degree degree
    ) {
        this.admissionYear = educationCreateRequest.getAdmissionYear();
        this.graduationYear = educationCreateRequest.getGraduationYear();
        this.universityName = universityName;
        this.majorName = majorName;
        this.degree = degree;
    }

}
