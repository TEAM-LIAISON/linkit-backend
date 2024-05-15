package liaison.linkit.team.domain.announcement;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import static jakarta.persistence.GenerationType.IDENTITY;
import static lombok.AccessLevel.PROTECTED;

@Entity
@Getter
@AllArgsConstructor
@NoArgsConstructor(access = PROTECTED)
// 팀원 공고
public class TeamMemberAnnouncement {
    @Id
    @GeneratedValue(strategy = IDENTITY)
    private Long id;

    // 주요 업무
    @Column(name = "main_business")
    private String mainBusiness;

    // 우대 사항
    @Column(name = "preferential_treatment")
    private String preferentialTreatment;

    // 지원 절차
    @Column(name = "application_process")
    private String applicationProcess;

}
