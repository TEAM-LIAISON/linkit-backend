package liaison.linkit.profile.domain;

import jakarta.persistence.*;
import liaison.linkit.profile.dto.request.AntecedentsUpdateRequest;
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
@Table(name = "antecedents")
// 이력
public class Antecedents {
    @Id
    @GeneratedValue(strategy = IDENTITY)
    @Column(name = "antecedents_id")
    private Long id;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "profile_id")
    private Profile profile;

    // 기업명(프로젝트명)
    @Column(nullable = false)
    private String projectName;

    // 직무(역할)
    @Column(nullable = false)
    private String projectRole;

    // 시작연도
    @Column(nullable = false)
    private int startYear;

    // 시작월
    @Column(nullable = false)
    private int startMonth;

    @Column(nullable = false)
    private int endYear;

    @Column(nullable = false)
    private int endMonth;

    @Column(nullable = false)
    private String antecedentsDescription;

    public static Antecedents of(
            final Profile profile,
            final String projectName,
            final String projectRole,
            final int startYear,
            final int startMonth,
            final int endYear,
            final int endMonth,
            final String antecedentsDescription
    ) {
        return new Antecedents(
                null,
                profile,
                projectName,
                projectRole,
                startYear,
                startMonth,
                endYear,
                endMonth,
                antecedentsDescription
        );
    }

    public void update(final AntecedentsUpdateRequest antecedentsUpdateRequest) {
        this.projectName = antecedentsUpdateRequest.getProjectName();
        this.projectRole = antecedentsUpdateRequest.getProjectRole();
        this.startYear = antecedentsUpdateRequest.getStartYear();
        this.startMonth = antecedentsUpdateRequest.getStartMonth();
        this.endYear = antecedentsUpdateRequest.getEndYear();
        this.endMonth = antecedentsUpdateRequest.getEndMonth();
    }
}
