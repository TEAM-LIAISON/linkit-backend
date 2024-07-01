package liaison.linkit.team.domain.miniprofile;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import static jakarta.persistence.FetchType.LAZY;
import static jakarta.persistence.GenerationType.IDENTITY;

@Entity
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class TeamMiniProfileKeyword {
    @Id
    @GeneratedValue(strategy = IDENTITY)
    @Column(name = "team_mini_profile_keyword_id")
    private Long id;

    // 일대다 관계
    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "team_mini_profile_id")
    private TeamMiniProfile teamMiniProfile;

    @Column(name = "team_keyword_names")
    private String teamKeywordNames;
}
