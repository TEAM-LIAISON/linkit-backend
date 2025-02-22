package liaison.linkit.profile.domain.education;

import static jakarta.persistence.FetchType.LAZY;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;

import liaison.linkit.common.domain.BaseDateTimeEntity;
import liaison.linkit.common.domain.University;
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
public class ProfileEducation extends BaseDateTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "profile_id")
    private Profile profile;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "university_id")
    private University university;

    // 전공명
    @Column(nullable = false, length = 50)
    private String majorName;

    @Column(nullable = false)
    private String admissionYear; // 입학년도

    private String graduationYear; // 졸업년도
    private boolean isAttendUniversity; // 학적 상태

    // 학력 설명
    @Column(length = 300)
    private String educationDescription;

    private boolean isEducationCertified;
    private boolean isEducationVerified;
    private String educationCertificationAttachFileName;
    private String educationCertificationAttachFilePath;

    // 수상 인증서 세팅 메서드
    public void setProfileEducationCertification(
            final boolean isEducationCertified,
            final boolean isEducationVerified,
            final String educationCertificationAttachFileName,
            final String educationCertificationAttachFilePath) {
        this.isEducationCertified = isEducationCertified;
        this.isEducationVerified = isEducationVerified;
        this.educationCertificationAttachFileName = educationCertificationAttachFileName;
        this.educationCertificationAttachFilePath = educationCertificationAttachFilePath;
    }
}
