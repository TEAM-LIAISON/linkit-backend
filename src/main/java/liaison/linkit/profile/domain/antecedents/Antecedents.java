package liaison.linkit.profile.domain.antecedents;

import static jakarta.persistence.FetchType.LAZY;
import static jakarta.persistence.GenerationType.IDENTITY;
import static lombok.AccessLevel.PROTECTED;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import liaison.linkit.profile.domain.Profile;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@AllArgsConstructor
@NoArgsConstructor(access = PROTECTED)
@Table(name = "antecedents")
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
}
