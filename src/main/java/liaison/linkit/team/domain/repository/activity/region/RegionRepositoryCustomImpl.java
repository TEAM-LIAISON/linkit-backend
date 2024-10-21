package liaison.linkit.team.domain.repository.activity.region;

import com.querydsl.jpa.impl.JPAQueryFactory;
import liaison.linkit.profile.domain.region.QRegion;
import liaison.linkit.profile.domain.region.Region;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RequiredArgsConstructor
@Slf4j
public class RegionRepositoryCustomImpl implements RegionRepositoryCustom {

    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public Region findByCityNameAndDivisionName(final String cityName, final String divisionName) {
        QRegion region = QRegion.region;

        return jpaQueryFactory
                .selectFrom(region)
                .where(
                        region.cityName.eq(cityName),
                        region.divisionName.eq(divisionName)
                )
                .fetchOne();

    }
}
