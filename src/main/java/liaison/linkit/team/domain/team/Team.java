package liaison.linkit.team.domain.team;


import static jakarta.persistence.EnumType.STRING;
import static jakarta.persistence.GenerationType.IDENTITY;
import static lombok.AccessLevel.PROTECTED;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import java.util.ArrayList;
import java.util.List;
import liaison.linkit.global.BaseEntity;
import liaison.linkit.team.domain.region.TeamRegion;
import liaison.linkit.team.domain.scale.TeamScale;
import liaison.linkit.team.domain.team.type.TeamStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.SQLRestriction;

@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = PROTECTED)
@SQLRestriction("status = 'USABLE'")
public class Team extends BaseEntity {

    @Id
    @GeneratedValue(strategy = IDENTITY)
    private Long id;

    @OneToMany(mappedBy = "team")
    private List<TeamScale> teamScales = new ArrayList<>();

    @OneToMany(mappedBy = "team")
    private List<TeamRegion> teamRegions = new ArrayList<>();

    @Column(nullable = false, length = 10)
    private String teamName;

    @Column(nullable = false, length = 50)
    private String teamCode;

    @Column(nullable = false, length = 100)
    private String teamShortDescription;

    private String teamLogoImagePath;
    private boolean isTeamPublic;

    @Column(nullable = false)
    @Enumerated(value = STRING)
    private TeamStatus teamStatus;

    public void setTeamLogoImagePath(final String teamLogoImagePath) {
        this.teamLogoImagePath = teamLogoImagePath;
    }

    public void setTeamName(final String teamName) {
        this.teamName = teamName;
    }

    public void updateTeam(
        final String teamName,
        final String teamCode,
        final String teamShortDescription,
        final Boolean isTeamPublic
    ) {
        this.teamName = teamName;
        this.teamCode = teamCode;
        this.teamShortDescription = teamShortDescription;
        this.isTeamPublic = isTeamPublic;
    }

    public void setTeamStatus(final TeamStatus teamStatus) {
        this.teamStatus = teamStatus;
    }
}
