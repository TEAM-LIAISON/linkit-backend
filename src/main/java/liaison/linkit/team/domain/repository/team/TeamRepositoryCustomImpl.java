package liaison.linkit.team.domain.repository.team;

import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RequiredArgsConstructor
@Slf4j
public class TeamRepositoryCustomImpl implements TeamRepositoryCustom {

    private final JPAQueryFactory jpaQueryFactory;

}
