package liaison.linkit.profile.domain.teambuilding;

import jakarta.persistence.*;
import liaison.linkit.profile.domain.Profile;
import liaison.linkit.profile.dto.request.ProfileTeamBuildingUpdateRequest;
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
public class ProfileTeamBuildingField {
    @Id
    @GeneratedValue(strategy = IDENTITY)
    private Long id;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "profile_id")
    private Profile profile;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "team_building_field_id")
    private TeamBuildingField teamBuildingField;

    public static ProfileTeamBuildingField of(
        final Profile profile,
        final TeamBuildingField teamBuildingField
    ){
        return new ProfileTeamBuildingField(
                null,
                profile,
                teamBuildingField
        );
    }

    public void update(final ProfileTeamBuildingUpdateRequest profileTeamBuildingUpdateRequest) {
        this.profile = profileTeamBuildingUpdateRequest.getProfile();
        this.teamBuildingField = profileTeamBuildingUpdateRequest.getTeamBuildingField();
    }
}
