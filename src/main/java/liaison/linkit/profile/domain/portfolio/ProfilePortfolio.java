package liaison.linkit.profile.domain.portfolio;

import static jakarta.persistence.EnumType.STRING;
import static jakarta.persistence.FetchType.LAZY;
import static jakarta.persistence.GenerationType.IDENTITY;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Enumerated;
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
public class ProfilePortfolio extends BaseDateTimeEntity {
    @Id
    @GeneratedValue(strategy = IDENTITY)
    private Long id;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "profile_id")
    private Profile profile;

    private String projectName; // 프로젝트명
    private String projectLineDescription; // 한줄소개

    @Column(nullable = false)
    @Enumerated(value = STRING)
    private ProjectSize projectSize; // 규모

    private int projectHeadCount; // 인원
    private String projectTeamComposition; // 팀 구성

    private String projectStartDate;
    private String projectEndDate;
    private boolean isProjectInProgress;

    private String projectLink; // 링크
    private String projectDescription; // 설명

    private String projectRepresentImagePath; // 대표 이미지

    public void updateProjectRepresentImagePath(final String projectRepresentImagePath) {
        this.projectRepresentImagePath = projectRepresentImagePath;
    }
}
