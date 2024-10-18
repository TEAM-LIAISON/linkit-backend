package liaison.linkit.profile.domain;

import static jakarta.persistence.FetchType.LAZY;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
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
public class ProfileLicense extends BaseDateTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "profile_id")
    private Profile profile;

    // 자격명
    @Column(nullable = false)
    private String licenseName;

    // 자격발급기관 (관련 부처)
    @Column(nullable = false)
    private String licenseInstitution;

    // 취득시기
    @Column(nullable = false)
    private String acquisitionDate;

    // 자격 설명
    private String licenseDescription;

    // 자격증 증명서 존재 유무
    private Boolean isLicenseCertified;

    // 자격증 증명서 인증 여부
    private Boolean isLicenseVerified;

    // 자격증 증명서 첨부 파일 이름
    private String licenseCertificationAttachFileName;

    // 자격증 증명서 첨부 파일 경로
    private String licenseCertificationAttachFilePath;

    // 자격증 증명서 설명 내용
    private String licenseCertificationDescription;
}
