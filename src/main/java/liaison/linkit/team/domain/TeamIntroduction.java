package liaison.linkit.team.domain;

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
public class TeamIntroduction {
    @Id
    @GeneratedValue(strategy = IDENTITY)
    @Column(name = "team_introduction_id")
    private Long id;

    @ManyToOne(fetch = LAZY)
    private TeamProfile teamProfile;

    @Column(name = "team_introduction_content")
    private String teamIntroductionContent;

}
