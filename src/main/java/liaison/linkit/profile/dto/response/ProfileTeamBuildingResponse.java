package liaison.linkit.profile.dto.response;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.List;

@Getter
@RequiredArgsConstructor
public class ProfileTeamBuildingResponse {

    private final List<String> teamBuildingFieldNames;

    public static ProfileTeamBuildingResponse of(final List<String> profileTeamBuildingFieldNames) {
        return new ProfileTeamBuildingResponse(
                profileTeamBuildingFieldNames
        );
    }

}
