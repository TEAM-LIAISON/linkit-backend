package liaison.linkit.team.domain;


import static jakarta.persistence.FetchType.LAZY;
import static jakarta.persistence.GenerationType.IDENTITY;
import static lombok.AccessLevel.PROTECTED;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import liaison.linkit.global.BaseEntity;
import liaison.linkit.profile.domain.region.Region;
import liaison.linkit.team.domain.scale.TeamScale;
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

    private String teamLogoImagePath;

    @Column(nullable = false)
    private String teamName;

    @Column(nullable = false)
    private String teamShortDescription;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "team_scale_id", nullable = false)
    private TeamScale teamScale;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "region_id", nullable = false)
    private Region region;

    private boolean isTeamLog;
    private boolean isTeamMemberAnnouncement;
    private boolean isTeamMember;
    private boolean isTeamProduct;
    private boolean isTeamHistory;

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


    public void setIsTeamLog(final boolean isTeamLog) {
        this.isTeamLog = isTeamLog;
    }

    public void setIsTeamMemberAnnouncement(final boolean isTeamMemberAnnouncement) {
        this.isTeamMemberAnnouncement = isTeamMemberAnnouncement;
    }

    public void setIsTeamMember(final boolean isTeamMember) {
        this.isTeamMember = isTeamMember;
    }

    public void setIsTeamProduct(final boolean isTeamProduct) {
        this.isTeamProduct = isTeamProduct;
    }

    public void setIsTeamHistory(final boolean isTeamHistory) {
        this.isTeamHistory = isTeamHistory;
    }
}
