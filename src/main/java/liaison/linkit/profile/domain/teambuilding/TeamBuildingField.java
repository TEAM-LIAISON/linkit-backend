package liaison.linkit.profile.domain.teambuilding;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import static jakarta.persistence.GenerationType.IDENTITY;

@Entity
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class TeamBuildingField {
    @Id
    @GeneratedValue(strategy = IDENTITY)
    @Column(name = "team_building_field_id")
    private Long id;

    @Column(name = "team_building_field_name", nullable = false)
    private String teamBuildingFieldName;

    public static TeamBuildingField of(
            final String teamBuildingFieldName
    ) {
        return new TeamBuildingField(
                null,
                teamBuildingFieldName
        );
    }
}
