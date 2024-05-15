package liaison.linkit.team.domain.miniprofile;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

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

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "industry_sector_id")
    private IndustrySector industrySector;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "team_size_id")
    private TeamSize teamSize;

    @Column(name = "team_name")
    private String teamName;

    @Column(name = "team_one_line_introduction")
    private String teamOneLineIntroduction;

    @Column(name = "teamLink")
    private String teamLink;


}
