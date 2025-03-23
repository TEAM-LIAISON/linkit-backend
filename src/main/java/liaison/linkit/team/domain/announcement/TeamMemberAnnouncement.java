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
import jakarta.persistence.OneToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;

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
    private Long id;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "team_id", nullable = false)
    private Team team;

    @OneToOne(mappedBy = "teamMemberAnnouncement")
    private AnnouncementPosition announcementPosition;

    @OneToOne(mappedBy = "teamMemberAnnouncement")
    private AnnouncementProjectType announcementProjectType;

    @OneToOne(mappedBy = "teamMemberAnnouncement")
    private AnnouncementWorkType announcementWorkType;

    @Column(nullable = false, length = 50)
    private String announcementTitle; // 공고 제목

    private String announcementEndDate; // 공고 마감 기간
    private boolean isPermanentRecruitment; // 상시 모집 여부

    private boolean isRegionFlexible; // 지역 무관

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

    @Column(name = "is_advertising_mail_sent", nullable = false) // 공고 메일 발송 여부
    private boolean isAdvertisingMailSent;

    // 공고 공개/비공개 설정
    private boolean isAnnouncementPublic;

    // 공고 진행/완료 여부
    private boolean isAnnouncementInProgress;

    // 기존(legacy) 공고임을 식별하는 컬럼 추가
    @Column(name = "is_legacy_announcement")
    private boolean isLegacyAnnouncement;

    @Column(nullable = false)
    private Long viewCount;

    public void setIsAnnouncementPublic(final boolean isAnnouncementPublic) {
        this.isAnnouncementPublic = isAnnouncementPublic;
    }

    @PrePersist
    @PreUpdate
    private void prePersistOrUpdate() {
        if (this.announcementEndDate != null && this.announcementEndDate.trim().isEmpty()) {
            this.announcementEndDate = null;
        }
    }

    // 광고 메일 발송 상태를 업데이트하는 메서드
    public void markAdvertisingMailAsSent() {
        this.isAdvertisingMailSent = true;
    }

    public void increaseViewCount() {
        this.viewCount++;
    }
}
