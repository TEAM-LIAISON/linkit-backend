package liaison.linkit.profile.domain.repository.education;

import com.querydsl.jpa.impl.JPAQueryFactory;
import liaison.linkit.profile.domain.education.Degree;
import liaison.linkit.profile.domain.education.QDegree;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.Optional;

@RequiredArgsConstructor
@Slf4j
public class DegreeRepositoryCustomImpl implements DegreeRepositoryCustom {

    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public Optional<Degree> findByDegreeName(final String degreeName) {
        QDegree degree = QDegree.degree;

        Degree result = jpaQueryFactory
                .selectFrom(degree)
                .where(degree.degreeName.eq(degreeName))
                .fetchOne();

        return Optional.ofNullable(result);
    }
}
