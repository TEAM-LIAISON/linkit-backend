package liaison.linkit.profile.domain.repository.position;

import com.querydsl.jpa.impl.JPAQueryFactory;
import liaison.linkit.profile.domain.position.ProfilePosition;
import liaison.linkit.profile.domain.position.QProfilePosition;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class ProfilePositionCustomRepositoryImpl implements ProfilePositionCustomRepository {
    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public boolean existsProfilePositionByProfileId(final Long profileId) {
        QProfilePosition qProfilePosition = QProfilePosition.profilePosition;

        Integer count =
                jpaQueryFactory
                        .selectOne()
                        .from(qProfilePosition)
                        .where(qProfilePosition.profile.id.eq(profileId))
                        .fetchFirst();

        return count != null;
    }

    @Override
    public ProfilePosition findProfilePositionByProfileId(final Long profileId) {
        QProfilePosition qProfilePosition = QProfilePosition.profilePosition;

        return jpaQueryFactory
                .selectFrom(qProfilePosition)
                .where(qProfilePosition.profile.id.eq(profileId))
                .fetchOne();
    }

    @Override
    public void deleteAllByProfileId(final Long profileId) {
        QProfilePosition qProfilePosition = QProfilePosition.profilePosition;

        jpaQueryFactory
                .delete(qProfilePosition)
                .where(qProfilePosition.profile.id.eq(profileId))
                .execute();
    }
}
