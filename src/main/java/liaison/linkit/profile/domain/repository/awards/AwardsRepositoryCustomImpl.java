package liaison.linkit.profile.domain.repository.awards;

import com.querydsl.jpa.impl.JPAQueryFactory;
import liaison.linkit.profile.domain.ProfileAwards;
import liaison.linkit.profile.domain.awards.QAwards;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

@RequiredArgsConstructor
@Slf4j
public class AwardsRepositoryCustomImpl implements AwardsRepositoryCustom {
    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public boolean existsByProfileId(Long profileId) {
        QAwards awards = QAwards.awards;

        Integer count = jpaQueryFactory
                .selectOne()
                .from(awards)
                .where(awards.profile.id.eq(profileId))
                .fetchFirst();

        return count != null;
    }

    @Override
    public List<ProfileAwards> findAllByProfileId(Long profileId) {
        QAwards awards = QAwards.awards;

        return jpaQueryFactory
                .selectFrom(awards)
                .where(awards.profile.id.eq(profileId))
                .fetch();
    }

    @Override
    public ProfileAwards findByProfileId(Long profileId) {
        QAwards awards = QAwards.awards;

        return jpaQueryFactory
                .selectFrom(awards)
                .where(awards.profile.id.eq(profileId))
                .fetchOne();
    }

    @Override
    public void deleteAllByProfileId(Long profileId) {
        QAwards awards = QAwards.awards;

        jpaQueryFactory
                .delete(awards)
                .where(awards.profile.id.eq(profileId))
                .execute();
    }
}
