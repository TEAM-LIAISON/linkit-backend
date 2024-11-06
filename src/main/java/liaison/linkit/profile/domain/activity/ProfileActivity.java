package liaison.linkit.profile.domain.activity;

import static jakarta.persistence.FetchType.LAZY;
import static jakarta.persistence.GenerationType.IDENTITY;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import liaison.linkit.common.domain.BaseDateTimeEntity;
import liaison.linkit.profile.domain.Profile;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class ProfileActivity extends BaseDateTimeEntity {

    @Id
    @GeneratedValue(strategy = IDENTITY)
    private Long id;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "profile_id")
    private Profile profile;

    private String activityName;
    private String activityRole;
    private String activityStartDate;
    private String activityEndDate;
    private boolean isActivityInProgress;
    private String activityDescription;

    // 인증하지 않은 사람 false
    private boolean isActivityCertified;
    // 인증하고 인증 대기 중
    private boolean isActivityVerified;
    // 파일 이름
    private String activityCertificationAttachFileName;
    // 파일 경로
    private String activityCertificationAttachFilePath;

    // 인증서
    public void setProfileActivityCertification(
            final boolean isActivityCertified,
            final boolean isActivityVerified,
            final String activityCertificationAttachFileName,
            final String activityCertificationAttachFilePath
    ) {
        this.isActivityCertified = isActivityCertified;
        this.isActivityVerified = isActivityVerified;
        this.activityCertificationAttachFileName = activityCertificationAttachFileName;
        this.activityCertificationAttachFilePath = activityCertificationAttachFilePath;
    }
}
