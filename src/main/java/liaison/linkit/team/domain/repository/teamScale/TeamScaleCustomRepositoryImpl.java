package liaison.linkit.team.domain.repository.teamScale;

import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RequiredArgsConstructor
@Slf4j
public class TeamScaleCustomRepositoryImpl implements TeamScaleCustomRepository {
    private final JPAQueryFactory jpaQueryFactory;
}
