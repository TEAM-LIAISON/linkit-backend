package liaison.linkit.resume.domain;

import jakarta.persistence.*;
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

    @Column(nullable = false)
    private int admissionYear;

    @Column(nullable = false)
    private int graduationYear;

    @Column(nullable = false)
    private String educationDescription;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "resume_id", nullable = false)
    private Resume resume;
}
