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
import liaison.linkit.common.domain.University;
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

    // 대학명
    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "university_name")
    private University university;

    // 전공명
    @Column(nullable = false)
    private String majorName;

    // 입학년도
    @Column(nullable = false)
    private int admissionYear;

    // 졸업년도
    private Integer graduationYear;

    // 학적 상태 (보류)

    // 학력 설명
    private String educationDescription;

    // 학력 증명서 존재 유무
    private Boolean isEducationCertified;

    // 학력 증명서 인증 여부
    private Boolean isEducationVerified;

    // 학력 증명서 첨부 파일 이름
    private String educationCertificationAttachFileName;

    // 학력 증명서 파일 경로
    private String educationCertificationAttachFilePath;

    // 학력 증명서 설명 내용
    private String educationCertificationDescription;
}
