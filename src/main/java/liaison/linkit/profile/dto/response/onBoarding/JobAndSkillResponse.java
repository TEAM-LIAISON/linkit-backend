package liaison.linkit.profile.dto.response.onBoarding;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.List;

@Getter
@RequiredArgsConstructor
public class JobAndSkillResponse {
    private final List<String> jobRoleNames;
    private final List<String> skillNames;

    public JobAndSkillResponse() {
        this.jobRoleNames = null;
        this.skillNames = null;
    }

    public static JobAndSkillResponse of(
            final List<String> skillNames,
            final List<String> jobRoleNames
    ) {
        return new JobAndSkillResponse(
                skillNames,
                jobRoleNames
        );
    }
}
