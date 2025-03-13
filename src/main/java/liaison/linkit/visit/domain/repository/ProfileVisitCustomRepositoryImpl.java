package liaison.linkit.visit.domain.repository;

import java.util.List;

import com.querydsl.jpa.impl.JPAQueryFactory;
import liaison.linkit.visit.domain.ProfileVisit;
import liaison.linkit.visit.domain.QProfileVisit;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RequiredArgsConstructor
@Slf4j
public class ProfileVisitCustomRepositoryImpl implements ProfileVisitCustomRepository {

    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public List<ProfileVisit> getProfileVisitsByVisitedProfileId(final Long visitedProfileId) {
        QProfileVisit qProfileVisit = QProfileVisit.profileVisit;

        return jpaQueryFactory
                .selectFrom(qProfileVisit)
                .where(qProfileVisit.visitedProfileId.eq(visitedProfileId))
                .fetch();
    }
}
