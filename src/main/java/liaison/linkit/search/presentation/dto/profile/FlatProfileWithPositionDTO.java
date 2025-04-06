package liaison.linkit.search.presentation.dto.profile;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class FlatProfileWithPositionDTO {
    private Long profileId;
    private String memberName;
    private String emailId;
    private String profileImagePath;
    private String majorPosition;
    private String subPosition;
    private String cityName;
    private String divisionName;
    private String profileStateName;
}
