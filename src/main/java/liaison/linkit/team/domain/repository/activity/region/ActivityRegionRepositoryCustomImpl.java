package liaison.linkit.team.domain.repository.activity.region;

import com.querydsl.jpa.impl.JPAQueryFactory;
import liaison.linkit.team.domain.activity.ActivityRegion;
import liaison.linkit.team.domain.activity.QActivityRegion;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@RequiredArgsConstructor
@Slf4j
public class ActivityRegionRepositoryCustomImpl implements ActivityRegionRepositoryCustom {

    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public boolean existsByTeamProfileId(final Long teamProfileId) {
        QActivityRegion activityRegion = QActivityRegion.activityRegion;

        Integer count = jpaQueryFactory
                .selectOne()
                .from(activityRegion)
                .where(activityRegion.teamProfile.id.eq(teamProfileId))
                .fetchFirst();

        return count != null;
    }

    @Override
    @Transactional
    public void deleteAllByTeamProfileId(final Long teamProfileId) {
        QActivityRegion activityRegion = QActivityRegion.activityRegion;

        jpaQueryFactory
                .delete(activityRegion)
                .where(activityRegion.teamProfile.id.eq(teamProfileId))
                .execute();
    }

    @Override
    public Optional<ActivityRegion> findByTeamProfileId(final Long teamProfileId) {
        QActivityRegion activityRegion = QActivityRegion.activityRegion;

        ActivityRegion result = jpaQueryFactory
                .selectFrom(activityRegion)
                .where(activityRegion.teamProfile.id.eq(teamProfileId))
                .fetchOne();

        return Optional.ofNullable(result);
    }
}
