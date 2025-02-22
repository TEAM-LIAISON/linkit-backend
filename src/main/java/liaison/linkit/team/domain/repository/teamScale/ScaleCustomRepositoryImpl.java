package liaison.linkit.team.domain.repository.teamScale;

import java.util.Optional;

import com.querydsl.jpa.impl.JPAQueryFactory;
import liaison.linkit.team.domain.scale.QScale;
import liaison.linkit.team.domain.scale.Scale;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RequiredArgsConstructor
@Slf4j
public class ScaleCustomRepositoryImpl implements ScaleCustomRepository {

    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public Optional<Scale> findByScaleName(final String scaleName) {
        QScale qScale = QScale.scale;

        return Optional.ofNullable(
                jpaQueryFactory
                        .selectFrom(qScale)
                        .where(qScale.scaleName.eq(scaleName))
                        .fetchOne());
    }
}
