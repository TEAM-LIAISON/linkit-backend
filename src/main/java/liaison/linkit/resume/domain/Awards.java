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
@Table(name = "awards")
public class Awards {
    @Id
    @GeneratedValue(strategy = IDENTITY)
    @Column(name = "awards_id")
    private Long id;

    @Column(nullable = false)
    private String awardsName;

    @Column(nullable = false)
    private String ranking;

    @Column(nullable = false)
    private int awardsYear;

    @Column(nullable = false)
    private int awardsMonth;

    @Column(nullable = false)
    private String awardsDescription;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "resume_id", nullable = false)
    private Resume resume;
}
