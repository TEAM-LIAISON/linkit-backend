package liaison.linkit.profile.dto.response;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.List;

@Getter
@RequiredArgsConstructor
public class ProfileTeamBuildingFieldResponse {

    private final List<String> teamBuildingFieldNames;

    public ProfileTeamBuildingFieldResponse() {
        this.teamBuildingFieldNames = null;
    }

    public static ProfileTeamBuildingFieldResponse of(final List<String> profileTeamBuildingFieldNames) {
        return new ProfileTeamBuildingFieldResponse(
                profileTeamBuildingFieldNames
        );
    }

}
