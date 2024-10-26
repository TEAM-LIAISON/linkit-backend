package liaison.linkit.team.domain.repository.teamMember;

import com.querydsl.jpa.impl.JPAQueryFactory;
import liaison.linkit.team.domain.QTeamMember;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RequiredArgsConstructor
@Slf4j
public class TeamMemberCustomRepositoryImpl implements TeamMemberCustomRepository {
    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public boolean existsTeamMemberByMemberIdAndTeamId(final Long memberId, final Long teamId) {
        QTeamMember qTeamMember = QTeamMember.teamMember;

        return jpaQueryFactory
                .selectOne()
                .from(qTeamMember)
                .where(qTeamMember.member.id.eq(memberId)
                        .and(qTeamMember.team.id.eq(teamId)))
                .fetchFirst() != null;
    }
}
