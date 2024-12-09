package liaison.linkit.team.domain.state;

import static jakarta.persistence.GenerationType.IDENTITY;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class TeamState {
    @Id
    @GeneratedValue(strategy = IDENTITY)
    private Long id;

    @Column(name = "team_state_name", nullable = false)
    private String teamStateName;

    public static TeamState of(
            final String teamStateName
    ) {
        return new TeamState(
                null,
                teamStateName
        );
    }
}
