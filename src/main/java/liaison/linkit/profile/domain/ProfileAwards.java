package liaison.linkit.profile.domain;

import static jakarta.persistence.FetchType.LAZY;
import static jakarta.persistence.GenerationType.IDENTITY;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import liaison.linkit.common.domain.BaseDateTimeEntity;
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
    @Column(nullable = false)
    private String awardsName;

    // 훈격(순위)
    @Column(nullable = false)
    private String awardsRanking;

    // 수상 시기
    @Column(nullable = false)
    private String awardsDate;

    // 주최 및 주관
    @Column(nullable = false)
    private String awardsOrganizer;
    
    // 수상 설명
    @Column(nullable = false)
    private String awardsDescription;

    private Boolean isAwardsCertified;
    private Boolean isAwardsVerified;
    private String awardsCertificationAttachFileName;
    private String awardsCertificationAttachFilePath;
    private String awardsCertificationDescription;
}
