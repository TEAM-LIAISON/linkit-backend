package liaison.linkit.team.domain;


import static jakarta.persistence.GenerationType.IDENTITY;
import static lombok.AccessLevel.PROTECTED;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import liaison.linkit.global.BaseEntity;
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

    private String teamName;
    private String teamShortDescription;
    private String teamLogoImagePath;
    private boolean isTeamPublic;

    public void setTeamLogoImagePath(final String teamLogoImagePath) {
        this.teamLogoImagePath = teamLogoImagePath;
    }

    public void setTeamName(final String teamName) {
        this.teamName = teamName;
    }

    public void setTeamShortDescription(final String teamShortDescription) {
        this.teamShortDescription = teamShortDescription;
    }

    public void updateTeam(
            final String teamName,
            final String teamShortDescription,
            final Boolean isTeamPublic
    ) {
        this.teamName = teamName;
        this.teamShortDescription = teamShortDescription;
        this.isTeamPublic = isTeamPublic;
    }
}
