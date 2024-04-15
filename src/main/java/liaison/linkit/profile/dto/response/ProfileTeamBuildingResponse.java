package liaison.linkit.profile.dto.response;

import liaison.linkit.profile.domain.Profile;
import liaison.linkit.profile.domain.teambuilding.ProfileTeamBuildingField;
import liaison.linkit.profile.domain.teambuilding.TeamBuildingField;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import static lombok.AccessLevel.PRIVATE;

@Getter
@RequiredArgsConstructor(access = PRIVATE)
public class ProfileTeamBuildingResponse {

    private final Long id;
    private final Profile profile;
    private final TeamBuildingField teamBuildingField;

    public static ProfileTeamBuildingResponse of(final ProfileTeamBuildingField profileTeamBuildingField) {
        return new ProfileTeamBuildingResponse(
                profileTeamBuildingField.getId(),
                profileTeamBuildingField.getProfile(),
                profileTeamBuildingField.getTeamBuildingField()
        );
    }
}
