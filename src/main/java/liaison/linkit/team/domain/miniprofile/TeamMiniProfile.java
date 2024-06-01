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
import static lombok.AccessLevel.PROTECTED;

@Entity
@Getter
@AllArgsConstructor
@NoArgsConstructor(access = PROTECTED)
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
    @Column(name = "mini_profile_title")
    private String miniProfileTitle;

    // 공고 업로드 기간
    @Column(name = "team_upload_period")
    private LocalDateTime teamUploadPeriod;

    // 공고 업로드 기간 (마감있음 / 계속 업로드)
    @Column(name = "team_upload_deadline")
    private Boolean teamUploadDeadline;

    // 팀 로고 링크 (저장 URL)
    @Column(name = "team_logo_image")
    private String teamLogoImage;

    // 팀 홍보 가치
    @Column(name = "team_value")
    private String teamValue;

    // 팀 세부 정보
    @Column(name = "team_detail_inform")
    private String teamDetailInform;


}
