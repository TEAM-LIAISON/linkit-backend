package liaison.linkit.profile.domain.awards;

import static jakarta.persistence.FetchType.LAZY;
import static jakarta.persistence.GenerationType.IDENTITY;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;

import liaison.linkit.common.domain.BaseDateTimeEntity;
import liaison.linkit.profile.domain.profile.Profile;
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
public class ProfileAwards extends BaseDateTimeEntity {

    @Id
    @GeneratedValue(strategy = IDENTITY)
    private Long id;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "profile_id")
    private Profile profile;

    // 대회 이름
    @Column(nullable = false, length = 50)
    private String awardsName;

    // 훈격(순위)
    @Column(nullable = false, length = 50)
    private String awardsRanking;

    // 수상 시기
    private String awardsDate;

    // 주최 및 주관
    @Column(nullable = false, length = 50)
    private String awardsOrganizer;

    // 수상 설명
    @Column(length = 300)
    private String awardsDescription;

    private boolean isAwardsCertified;
    private boolean isAwardsVerified;
    private String awardsCertificationAttachFileName;
    private String awardsCertificationAttachFilePath;

    // 수상 인증서 세팅 메서드
    public void setProfileAwardsCertification(
            final boolean isAwardsCertified,
            final boolean isAwardsVerified,
            final String awardsCertificationAttachFileName,
            final String awardsCertificationAttachFilePath) {
        this.isAwardsCertified = isAwardsCertified;
        this.isAwardsVerified = isAwardsVerified;
        this.awardsCertificationAttachFileName = awardsCertificationAttachFileName;
        this.awardsCertificationAttachFilePath = awardsCertificationAttachFilePath;
    }
}
