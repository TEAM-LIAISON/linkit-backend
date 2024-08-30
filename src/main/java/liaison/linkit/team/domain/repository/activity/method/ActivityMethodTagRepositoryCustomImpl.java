package liaison.linkit.team.domain.repository.activity.method;

import com.querydsl.jpa.impl.JPAQueryFactory;
import liaison.linkit.team.domain.activity.ActivityMethodTag;
import liaison.linkit.team.domain.activity.QActivityMethodTag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

@RequiredArgsConstructor
@Slf4j
public class ActivityMethodTagRepositoryCustomImpl implements ActivityMethodTagRepositoryCustom{

    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public List<ActivityMethodTag> findActivityMethodTagByActivityTagNames(final List<String> activityTagNames) {
        QActivityMethodTag activityMethodTag = QActivityMethodTag.activityMethodTag;

        return jpaQueryFactory
                .selectFrom(activityMethodTag)
                .where(activityMethodTag.activityTagName.in(activityTagNames))
                .fetch();
    }
}
