package liaison.linkit.team.domain.repository.teamMember;

import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RequiredArgsConstructor
@Slf4j
public class TeamMemberRepositoryCustomImpl implements TeamMemberRepositoryCustom{
    private final JPAQueryFactory jpaQueryFactory;
}
