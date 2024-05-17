package liaison.linkit.team.domain.miniprofile;

import jakarta.persistence.*;
import liaison.linkit.team.domain.TeamProfile;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

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


//    public static TeamMiniProfile of(
//            final IndustrySector industrySector,
//            final TeamSize teamSize,
//            final String teamName,
//            final String teamOneLineIntroduction,
//            final String teamLink
//    ) {
//        return new TeamMiniProfile(
//                null,
//                industrySector,
//                teamSize,
//                teamName,
//                teamOneLineIntroduction,
//                teamLink
//        );
//    }
}
