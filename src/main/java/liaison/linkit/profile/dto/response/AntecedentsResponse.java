package liaison.linkit.profile.dto.response;

import liaison.linkit.profile.domain.Antecedents;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class AntecedentsResponse {
    private final Long id;
    private final String projectName;
    private final String projectRole;
    private final int startYear;
    private final int startMonth;
    private final int endYear;
    private final int endMonth;
    private final boolean retirement;
    private final String antecedentsDescription;

    public static AntecedentsResponse of(final Antecedents antecedents){
        return new AntecedentsResponse(
                antecedents.getId(),
                antecedents.getProjectName(),
                antecedents.getProjectRole(),
                antecedents.getStartYear(),
                antecedents.getStartMonth(),
                antecedents.getEndYear(),
                antecedents.getEndMonth(),
                antecedents.isRetirement(),
                antecedents.getAntecedentsDescription()
        );
    }

    public static AntecedentsResponse personalAntecedents(final Antecedents antecedents) {
        return new AntecedentsResponse(
                antecedents.getId(),
                antecedents.getProjectName(),
                antecedents.getProjectRole(),
                antecedents.getStartYear(),
                antecedents.getStartMonth(),
                antecedents.getEndYear(),
                antecedents.getEndMonth(),
                antecedents.isRetirement(),
                antecedents.getAntecedentsDescription()
        );
    }

}

