package liaison.linkit.profile.domain.repository.region;

import java.util.Optional;

import com.querydsl.jpa.impl.JPAQueryFactory;
import liaison.linkit.profile.domain.region.ProfileRegion;
import liaison.linkit.profile.domain.region.QProfileRegion;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RequiredArgsConstructor
@Slf4j
public class ProfileRegionRepositoryCustomImpl implements ProfileRegionRepositoryCustom {

    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public Optional<ProfileRegion> findProfileRegionByProfileId(final Long profileId) {
        QProfileRegion qProfileRegion = QProfileRegion.profileRegion;

        ProfileRegion profileRegion =
                jpaQueryFactory
                        .selectFrom(qProfileRegion)
                        .where(qProfileRegion.profile.id.eq(profileId))
                        .fetchOne();

        return Optional.ofNullable(profileRegion);
    }

    @Override
    public boolean existsProfileRegionByProfileId(final Long profileId) {
        QProfileRegion qProfileRegion = QProfileRegion.profileRegion;

        Integer count =
                jpaQueryFactory
                        .selectOne()
                        .from(qProfileRegion)
                        .where(qProfileRegion.profile.id.eq(profileId))
                        .fetchFirst();

        return count != null;
    }

    @Override
    public void deleteByProfileId(Long profileId) {
        final QProfileRegion qProfileRegion = QProfileRegion.profileRegion;

        jpaQueryFactory
                .delete(qProfileRegion)
                .where(qProfileRegion.profile.id.eq(profileId))
                .execute();
    }
}
