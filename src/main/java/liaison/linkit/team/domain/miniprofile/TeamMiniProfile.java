package liaison.linkit.team.domain.miniprofile;

import jakarta.persistence.*;
import liaison.linkit.team.domain.TeamProfile;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

import static jakarta.persistence.CascadeType.ALL;
import static jakarta.persistence.FetchType.LAZY;
import static jakarta.persistence.GenerationType.IDENTITY;

@Entity
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class TeamMiniProfile {

    @Id
    @GeneratedValue(strategy = IDENTITY)
    @Column(name = "team_mini_profile_id")
    private Long id;

    @OneToOne(cascade = ALL, orphanRemoval = true, fetch = LAZY)
    @JoinColumn(name = "team_profile_id", unique = true)
    private TeamProfile teamProfile;

    // 팀 분야
    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "industry_sector_id")
    private IndustrySector industrySector;

    // 팀 규모
    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "team_size_id")
    private TeamScale teamScale;

    // 팀명
    @Column(name = "team_name")
    private String teamName;

    // 제목을 입력해주세요 항목
    @Column(name = "team_profile_title")
    private String teamProfileTitle;

    // 공고 업로드 기간
    @Column(name = "team_upload_period")
    private LocalDate teamUploadPeriod;

    // 공고 업로드 기간 (마감있음 / 계속 업로드)
    @Column(name = "team_upload_deadline")
    private Boolean teamUploadDeadline;

    // 팀 로고 링크 (저장 URL)
    @Column(name = "team_logo_image_url")
    private String teamLogoImageUrl;

    // 팀 홍보 가치
    @Column(name = "team_value")
    private String teamValue;

    // 팀 세부 정보
    @Column(name = "team_detail_inform")
    private String teamDetailInform;

    public static TeamMiniProfile of(
            final TeamProfile teamProfile,
            final IndustrySector industrySector,
            final TeamScale teamScale,
            final String teamName,
            final String teamProfileTitle,
            final LocalDate teamUploadPeriod,
            final Boolean teamUploadDeadline,
            final String teamLogoImageUrl,
            final String teamValue,
            final String teamDetailInform
    ) {
        return new TeamMiniProfile(
                null,
                teamProfile,
                industrySector,
                teamScale,
                teamName,
                teamProfileTitle,
                teamUploadPeriod,
                teamUploadDeadline,
                teamLogoImageUrl,
                teamValue,
                teamDetailInform
        );
    }

    public void onBoardingTeamMiniProfile(
            final String teamProfileTitle,
            final LocalDate teamUploadPeriod,
            final boolean teamUploadDeadline,
            final String teamLogoImageUrl,
            final String teamValue,
            final String teamDetailInform
    ) {
        this.teamProfileTitle = teamProfileTitle;
        this.teamUploadPeriod = teamUploadPeriod;
        this.teamUploadDeadline = teamUploadDeadline;
        this.teamLogoImageUrl = teamLogoImageUrl;
        this.teamValue = teamValue;
        this.teamDetailInform = teamDetailInform;
    }
}
