package liaison.linkit.team.domain.announcement;

import static jakarta.persistence.GenerationType.IDENTITY;
import static lombok.AccessLevel.PROTECTED;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import liaison.linkit.global.BaseEntity;
import liaison.linkit.team.dto.request.announcement.TeamMemberAnnouncementRequest;
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

    public static TeamMemberAnnouncement of(
            final String mainBusiness,
            final String applicationProcess
    ) {
        return new TeamMemberAnnouncement(
                null,
                mainBusiness,
                applicationProcess
        );
    }

    public void update(
            final TeamMemberAnnouncementRequest teamMemberAnnouncementRequest
    ) {
        this.mainBusiness = teamMemberAnnouncementRequest.getMainBusiness();
        this.applicationProcess = teamMemberAnnouncementRequest.getApplicationProcess();
    }
}
