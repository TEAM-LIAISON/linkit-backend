package liaison.linkit.team.domain.repository.miniprofile.industrySector;

import com.querydsl.jpa.impl.JPAQueryFactory;
import liaison.linkit.team.domain.miniprofile.IndustrySector;
import liaison.linkit.team.domain.miniprofile.QIndustrySector;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RequiredArgsConstructor
@Slf4j
public class IndustrySectorRepositoryCustomImpl implements IndustrySectorRepositoryCustom {

    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public IndustrySector findBySectorName(final String sectorName) {
        QIndustrySector industrySector = QIndustrySector.industrySector;

        return jpaQueryFactory
                .selectFrom(industrySector)
                .where(industrySector.sectorName.eq(sectorName))
                .fetchOne();
    }
}
