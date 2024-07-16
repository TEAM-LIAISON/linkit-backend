package liaison.linkit.team.dto.response.activity;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.List;

@Getter
@RequiredArgsConstructor
public class ActivityMethodResponse {

    private final List<String> activityTagName;

    public static ActivityMethodResponse of(final List<String> activityTagNames) {
        return new ActivityMethodResponse(
                activityTagNames
        );
    }
}
