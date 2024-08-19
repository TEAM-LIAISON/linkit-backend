package liaison.linkit.team.domain.repository.activity.method;

import com.querydsl.jpa.impl.JPAQueryFactory;
import liaison.linkit.team.domain.activity.ActivityMethod;
import liaison.linkit.team.domain.activity.QActivityMethod;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@RequiredArgsConstructor
@Slf4j
public class ActivityMethodRepositoryCustomImpl implements ActivityMethodRepositoryCustom {
    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public boolean existsByTeamProfileId(final Long teamProfileId) {
        QActivityMethod activityMethod = QActivityMethod.activityMethod;

        Integer count = jpaQueryFactory
                .selectOne()
                .from(activityMethod)
                .where(activityMethod.teamProfile.id.eq(teamProfileId))
                .fetchFirst();

        return count != null;
    }

    @Override
    @Transactional
    public void deleteAllByTeamProfileId(final Long teamProfileId) {
        QActivityMethod activityMethod = QActivityMethod.activityMethod;

        jpaQueryFactory
                .delete(activityMethod)
                .where(activityMethod.teamProfile.id.eq(teamProfileId))
                .execute();
    }

    @Override
    public List<ActivityMethod> findAllByTeamProfileId(final Long teamProfileId) {
        QActivityMethod activityMethod = QActivityMethod.activityMethod;

        return jpaQueryFactory
                .selectFrom(activityMethod)
                .where(activityMethod.teamProfile.id.eq(teamProfileId))
                .fetch();
    }

}
