package liaison.linkit.profile.dto.response.antecedents;

import liaison.linkit.profile.domain.antecedents.Antecedents;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class AntecedentsResponse {
    private final Long id;
    private final String projectName;
    private final String projectRole;
    private final String startDate;
    private final String endDate;
    private final boolean retirement;
    private final String antecedentsDescription;

    public static AntecedentsResponse of(final Antecedents antecedents){
        return new AntecedentsResponse(
                antecedents.getId(),
                antecedents.getProjectName(),
                antecedents.getProjectRole(),
                antecedents.getStartDate(),
                antecedents.getEndDate(),
                antecedents.isRetirement(),
                antecedents.getAntecedentsDescription()
        );
    }

    public static AntecedentsResponse personalAntecedents(final Antecedents antecedents) {
        return new AntecedentsResponse(
                antecedents.getId(),
                antecedents.getProjectName(),
                antecedents.getProjectRole(),
                antecedents.getStartDate(),
                antecedents.getEndDate(),
                antecedents.isRetirement(),
                antecedents.getAntecedentsDescription()
        );
    }

}

