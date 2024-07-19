package liaison.linkit.matching.dto.response.existence;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ExistenceProfileResponse {
    final Boolean isPrivateProfileMatchingAllow;
    final Boolean isTeamProfileMatchingAllow;
}
