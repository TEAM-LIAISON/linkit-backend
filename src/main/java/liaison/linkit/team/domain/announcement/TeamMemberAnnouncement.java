package liaison.linkit.team.domain.announcement;

import static jakarta.persistence.FetchType.LAZY;
import static jakarta.persistence.GenerationType.IDENTITY;
import static lombok.AccessLevel.PROTECTED;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Lob;
import jakarta.persistence.ManyToOne;
import liaison.linkit.global.BaseEntity;
import liaison.linkit.team.domain.team.Team;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = PROTECTED)
public class TeamMemberAnnouncement extends BaseEntity {

    @Id
    @GeneratedValue(strategy = IDENTITY)
    @Column(name = "team_member_announcement_id")
    private Long id;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "team_id")
    private Team team;

    @Column(nullable = false, length = 100)
    private String announcementTitle;                   // 공고 제목

    private String announcementEndDate;                 // 공고 마감 기간
    private boolean isPermanentRecruitment;             // 상시 모집 여부

    private boolean isRegionFlexible;                   // 지역 무관

    @Lob
    @Column(nullable = false, columnDefinition = "TEXT")
    private String mainTasks;

    @Lob
    @Column(nullable = false, columnDefinition = "TEXT")
    private String workMethod;

    @Lob
    @Column(nullable = false, columnDefinition = "TEXT")
    private String idealCandidate;

    @Lob
    @Column(columnDefinition = "TEXT")
    private String preferredQualifications;

    @Lob
    @Column(columnDefinition = "TEXT")
    private String joiningProcess;

    @Lob
    @Column(columnDefinition = "TEXT")
    private String benefits;

    private boolean isAnnouncementPublic;               // 공고 공개/비공개 설정
    private boolean isAnnouncementInProgress;           // 공고 진행/완료 여부

    public void setIsAnnouncementPublic(final boolean isAnnouncementPublic) {
        this.isAnnouncementPublic = isAnnouncementPublic;
    }
}
