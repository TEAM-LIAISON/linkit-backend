package liaison.linkit.team.domain.repository.workType;

import java.util.Optional;

import com.querydsl.jpa.impl.JPAQueryFactory;
import liaison.linkit.team.domain.workType.QWorkType;
import liaison.linkit.team.domain.workType.WorkType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
@Slf4j
public class WorkTypeCustomRepositoryImpl implements WorkTypeCustomRepository {

    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public Optional<WorkType> findByWorkTypeName(final String workTypeName) {
        final QWorkType qWorkType = QWorkType.workType;

        return Optional.ofNullable(
                jpaQueryFactory
                        .selectFrom(qWorkType)
                        .where(qWorkType.workTypeName.eq(workTypeName))
                        .fetchOne());
    }
}
