package liaison.linkit.team.domain.miniprofile;

import jakarta.persistence.*;
import liaison.linkit.team.domain.TeamProfile;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

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

    @Column(name = "is_team_activate")
    private Boolean isTeamActivate;

    // 팀 로고 링크 (저장 URL)
    @Column(name = "team_logo_image_url")
    private String teamLogoImageUrl;

    // 생성 날짜
    @Column(updatable = false)
    private LocalDateTime createdDate;

    // 엔티티가 처음 저장될 때 createdDate를 현재 시간으로 설정
    @PrePersist
    protected void onCreate() {
        this.createdDate = LocalDateTime.now();
    }

    public static TeamMiniProfile of(
            final TeamProfile teamProfile,
            final IndustrySector industrySector,
            final TeamScale teamScale,
            final String teamName,
            final String teamProfileTitle,
            final Boolean isTeamActivate,
            final String teamLogoImageUrl
    ) {
        return new TeamMiniProfile(
                null,
                teamProfile,
                industrySector,
                teamScale,
                teamName,
                teamProfileTitle,
                isTeamActivate,
                teamLogoImageUrl,
                null
        );
    }

    public void onBoardingTeamMiniProfile(
            final String teamProfileTitle,
            final boolean isTeamActivate,
            final String teamLogoImageUrl
    ) {
        this.teamProfileTitle = teamProfileTitle;
        this.isTeamActivate = isTeamActivate;
        this.teamLogoImageUrl = teamLogoImageUrl;
    }
}
