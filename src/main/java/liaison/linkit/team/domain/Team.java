package liaison.linkit.team.domain;


import static jakarta.persistence.FetchType.LAZY;
import static jakarta.persistence.GenerationType.IDENTITY;
import static lombok.AccessLevel.PROTECTED;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import java.util.ArrayList;
import java.util.List;
import liaison.linkit.global.BaseEntity;
import liaison.linkit.profile.domain.region.Region;
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

    @OneToMany(mappedBy = "team", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<TeamMember> teamMembers = new ArrayList<>();

    private String teamLogoImagePath;

    @Column(nullable = false)
    private String teamName;

    @Column(nullable = false)
    private String teamShortDescription;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "region_id", nullable = false)
    private Region region;

    public void setTeamLogoImagePath(final String teamLogoImagePath) {
        this.teamLogoImagePath = teamLogoImagePath;
    }

    public void setTeamName(final String teamName) {
        this.teamName = teamName;
    }

    public void setTeamShortDescription(final String teamShortDescription) {
        this.teamShortDescription = teamShortDescription;
    }

    public void setRegion(final Region region) {
        this.region = region;
    }
}
