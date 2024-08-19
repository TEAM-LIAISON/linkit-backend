package liaison.linkit.team.domain.repository.activity.method;

import liaison.linkit.team.domain.activity.ActivityMethodTag;

import java.util.List;

public interface ActivityMethodTagRepositoryCustom {
    List<ActivityMethodTag> findActivityMethodTagByActivityTagNames(final List<String> activityTagNames);
}
