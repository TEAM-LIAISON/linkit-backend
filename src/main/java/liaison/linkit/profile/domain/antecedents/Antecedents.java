package liaison.linkit.profile.domain.antecedents;

import jakarta.persistence.*;
import liaison.linkit.profile.domain.Profile;
import liaison.linkit.profile.dto.request.antecedents.AntecedentsCreateRequest;
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

    // 포지션
    @Column(nullable = false)
    private String projectRole;

    // 회사/프로젝트명
    @Column(nullable = false)
    private String projectName;

    // 시작 연도
    @Column(nullable = false)
    private int startYear;

    // 시작월
    @Column(nullable = false)
    private int startMonth;

    // 종료 연도
    @Column(nullable = false)
    private int endYear;

    // 종료 월
    @Column(nullable = false)
    private int endMonth;

    // 진행 및 종료 여부
    @Column(nullable = false)
    private boolean retirement;

    // 경력 설명
    @Column
    private String antecedentsDescription;

    public static Antecedents of(
            final Profile profile,
            final String projectName,
            final String projectRole,
            final int startYear,
            final int startMonth,
            final int endYear,
            final int endMonth,
            final boolean retirement,
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
                retirement,
                antecedentsDescription
        );
    }

    public void update(final AntecedentsCreateRequest antecedentsCreateRequest) {
        this.projectName = antecedentsCreateRequest.getProjectName();
        this.projectRole = antecedentsCreateRequest.getProjectRole();
        this.startYear = antecedentsCreateRequest.getStartYear();
        this.startMonth = antecedentsCreateRequest.getStartMonth();
        this.endYear = antecedentsCreateRequest.getEndYear();
        this.endMonth = antecedentsCreateRequest.getEndMonth();
        this.retirement = antecedentsCreateRequest.isRetirement();
        this.antecedentsDescription = antecedentsCreateRequest.getAntecedentsDescription();
    }
}
