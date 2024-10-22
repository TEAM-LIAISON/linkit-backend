package liaison.linkit.team.domain.announcement;

import static jakarta.persistence.GenerationType.IDENTITY;
import static lombok.AccessLevel.PROTECTED;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import liaison.linkit.global.BaseEntity;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@AllArgsConstructor
@NoArgsConstructor(access = PROTECTED)
public class TeamMemberAnnouncement extends BaseEntity {

    @Id
    @GeneratedValue(strategy = IDENTITY)
    @Column(name = "team_member_announcement_id")
    private Long id;

    // 2. 주요 업무
    @Column(name = "main_business")
    private String mainBusiness;

    // 4. 지원 절차
    @Column(name = "application_process")
    private String applicationProcess;
}
