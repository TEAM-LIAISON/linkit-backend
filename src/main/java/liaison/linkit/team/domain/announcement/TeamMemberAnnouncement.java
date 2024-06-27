package liaison.linkit.team.domain.announcement;

import jakarta.persistence.*;
import liaison.linkit.team.domain.TeamProfile;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import static jakarta.persistence.CascadeType.ALL;
import static jakarta.persistence.FetchType.LAZY;
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

    @ManyToOne(fetch = LAZY, cascade = ALL)
    @JoinColumn(name = "team_profile_id")
    private TeamProfile teamProfile;

    // 주요 업무
    @Column(name = "main_business")
    private String mainBusiness;

    // 지원 절차
    @Column(name = "application_process")
    private String applicationProcess;


}
