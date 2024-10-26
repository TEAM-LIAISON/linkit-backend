package liaison.linkit.profile.domain.repository.awards;

import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RequiredArgsConstructor
@Slf4j
public class AwardsRepositoryCustomImpl implements AwardsRepositoryCustom {
    private final JPAQueryFactory jpaQueryFactory;
}
