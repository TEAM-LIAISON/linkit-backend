package liaison.linkit.team.domain.activity;

import jakarta.persistence.*;
import liaison.linkit.team.domain.miniprofile.TeamMiniProfile;
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
public class ActivityMethod {

    @Id
    @GeneratedValue(strategy = IDENTITY)
    private Long id;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "team_mini_profile_id")
    private TeamMiniProfile teamMiniProfile;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "activity_region_id")
    private Region region;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "activity_method_tag_id")
    private ActivityMethodTag activityMethodTag;
}
