package liaison.linkit.team.domain.attach;

import jakarta.persistence.*;
import liaison.linkit.team.domain.TeamProfile;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import static jakarta.persistence.FetchType.LAZY;
import static jakarta.persistence.GenerationType.IDENTITY;

@Entity
@Getter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "team_attach_url")
public class TeamAttachUrl {

    @Id
    @GeneratedValue(strategy = IDENTITY)
    @Column(name = "team_attach_url_id")
    private Long id;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "team_profile_id")
    private TeamProfile teamProfile;

    @Column(nullable = false)
    private String teamAttachUrlName;

    @Column(nullable = false)
    private String teamAttachUrlPath;

    public static TeamAttachUrl of(
            final TeamProfile teamProfile,
            final String teamAttachUrlName,
            final String teamAttachUrlPath
    ) {
        return new TeamAttachUrl(
                null,
                teamProfile,
                teamAttachUrlName,
                teamAttachUrlPath
        );
    }


}
