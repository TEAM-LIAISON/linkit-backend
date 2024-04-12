package liaison.linkit.profile.domain.teambuilding;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.NoArgsConstructor;

import static jakarta.persistence.GenerationType.IDENTITY;
import static lombok.AccessLevel.PROTECTED;

@Entity
@Getter
@NoArgsConstructor(access = PROTECTED)
public class TeamBuildingField {
    @Id
    @GeneratedValue(strategy = IDENTITY)
    @Column(name = "team_building_field_id")
    private Long id;

    @Column(name = "team_building_field_name", nullable = false)
    private String TemaBuildingFieldName;
}
