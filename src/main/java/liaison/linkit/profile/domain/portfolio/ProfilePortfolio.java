package liaison.linkit.profile.domain.portfolio;

import static jakarta.persistence.EnumType.STRING;
import static jakarta.persistence.FetchType.LAZY;
import static jakarta.persistence.GenerationType.IDENTITY;

import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;

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
public class ProfilePortfolio extends BaseDateTimeEntity {

    @Id
    @GeneratedValue(strategy = IDENTITY)
    private Long id;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "profile_id", nullable = false)
    private Profile profile;

    @Column(nullable = false, length = 50) // 제목 길이 제한 추가 (50자)
    private String projectName; // 프로젝트명

    @Column(nullable = false, length = 100) // 제목 길이 제한 추가 (50자)
    private String projectLineDescription; // 한줄소개

    @Column(nullable = false)
    @Enumerated(value = STRING)
    private ProjectSize projectSize; // 규모

    private int projectHeadCount; // 인원

    @Column(length = 50) // 제목 길이 제한 추가 (50자)
    private String projectTeamComposition; // 팀 구성

    @Column(nullable = false)
    private String projectStartDate;

    private String projectEndDate;
    private boolean isProjectInProgress;

    @OneToMany(mappedBy = "profilePortfolio", fetch = FetchType.LAZY)
    @Builder.Default
    private List<ProjectRoleContribution> projectRoleContributions = new ArrayList<>();

    @Column(length = 500) // 제목 길이 제한 추가 (50자)
    private String projectDescription; // 설명

    private String projectRepresentImagePath; // 대표 이미지

    public void updateProjectRepresentImagePath(final String projectRepresentImagePath) {
        this.projectRepresentImagePath = projectRepresentImagePath;
    }
}
