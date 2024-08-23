package liaison.linkit.profile.domain.repository.region;

import com.querydsl.jpa.impl.JPAQueryFactory;
import liaison.linkit.profile.domain.region.ProfileRegion;
import liaison.linkit.profile.domain.region.QProfileRegion;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.Optional;

@RequiredArgsConstructor
@Slf4j
public class ProfileRegionRepositoryCustomImpl implements ProfileRegionRepositoryCustom{

    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public Optional<ProfileRegion> findByProfileId(Long profileId) {
        QProfileRegion profileRegion = QProfileRegion.profileRegion;

        ProfileRegion result = jpaQueryFactory
                .selectFrom(profileRegion)
                .where(profileRegion.profile.id.eq(profileId))
                .fetchOne();

        return Optional.ofNullable(result);
    }

    @Override
    public boolean existsByProfileId(Long profileId) {
        QProfileRegion profileRegion = QProfileRegion.profileRegion;

        Integer count = jpaQueryFactory
                .selectOne()
                .from(profileRegion)
                .where(profileRegion.profile.id.eq(profileId))
                .fetchFirst();

        return count != null;
    }

    @Override
    public void deleteByProfileId(Long profileId) {
        QProfileRegion profileRegion = QProfileRegion.profileRegion;

        jpaQueryFactory
                .delete(profileRegion)
                .where(profileRegion.profile.id.eq(profileId))
                .execute();
    }
}
