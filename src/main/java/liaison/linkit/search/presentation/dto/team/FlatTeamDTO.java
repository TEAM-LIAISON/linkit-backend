package liaison.linkit.search.presentation.dto.team;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class FlatTeamDTO {
    private Long teamId;

    private String teamName;
    private String teamCode;
    private String teamShortDescription;
    private String teamLogoImagePath;

    private String teamScaleName;
    private String cityName;
    private String divisionName;

    private String teamCurrentStateName;
}
