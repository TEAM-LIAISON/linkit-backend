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

    // 회사/프로젝트명
    @Column(nullable = false)
    private String projectName;

    // 포지션
    @Column(nullable = false)
    private String projectRole;

    // 시작 날짜
    @Column(nullable = false)
    private String startDate;

    // 종료 날짜
    @Column(nullable = false)
    private String endDate;

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
            final String startDate,
            final String endDate,
            final boolean retirement,
            final String antecedentsDescription
    ) {
        return new Antecedents(
                null,
                profile,
                projectName,
                projectRole,
                startDate,
                endDate,
                retirement,
                antecedentsDescription
        );
    }

    public void update(final AntecedentsCreateRequest antecedentsCreateRequest) {
        this.projectName = antecedentsCreateRequest.getProjectName();
        this.projectRole = antecedentsCreateRequest.getProjectRole();
        this.startDate = antecedentsCreateRequest.getStartDate();
        this.endDate = antecedentsCreateRequest.getEndDate();
        this.retirement = antecedentsCreateRequest.isRetirement();
        this.antecedentsDescription = antecedentsCreateRequest.getAntecedentsDescription();
    }
}
