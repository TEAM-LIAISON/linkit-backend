package liaison.linkit.team.domain.announcement;

import jakarta.persistence.*;
import liaison.linkit.profile.domain.role.JobRole;
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
public class TeamMemberAnnouncementJobRole {

    @Id
    @GeneratedValue(strategy = IDENTITY)
    @Column(name = "team_member_announcement_job_role_id")
    private Long id;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "team_member_announcement_id")
    private TeamMemberAnnouncement teamMemberAnnouncement;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "job_role_id")
    private JobRole jobRole;
}
