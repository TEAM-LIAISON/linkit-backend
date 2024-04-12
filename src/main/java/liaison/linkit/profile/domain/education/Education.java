package liaison.linkit.profile.domain.education;

import jakarta.persistence.*;
import liaison.linkit.member.domain.Member;
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
    @JoinColumn(name = "member_id")
    private Member member;

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
}
