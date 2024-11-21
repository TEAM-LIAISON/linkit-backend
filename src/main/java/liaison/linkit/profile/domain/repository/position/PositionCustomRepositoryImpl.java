package liaison.linkit.profile.domain.repository.position;

import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.Optional;
import liaison.linkit.common.domain.Position;
import liaison.linkit.common.domain.QPosition;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class PositionCustomRepositoryImpl implements PositionCustomRepository {
    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public Optional<Position> findByMajorPositionAndSubPosition(String majorPosition, String subPosition) {
        final QPosition qPosition = QPosition.position;

        return Optional.ofNullable(
                jpaQueryFactory
                        .selectFrom(qPosition)
                        .where(qPosition.majorPosition.eq(majorPosition)
                                .and(qPosition.subPosition.eq(subPosition)))
                        .fetchOne()
        );
    }
}
