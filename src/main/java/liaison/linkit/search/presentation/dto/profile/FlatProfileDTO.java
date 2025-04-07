package liaison.linkit.search.presentation.dto.profile;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class FlatProfileDTO {
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
