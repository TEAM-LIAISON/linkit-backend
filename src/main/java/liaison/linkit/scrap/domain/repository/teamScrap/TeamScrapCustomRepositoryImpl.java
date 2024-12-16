package liaison.linkit.scrap.domain.repository.teamScrap;

import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.List;
import java.util.Optional;
import liaison.linkit.scrap.domain.QTeamScrap;
import liaison.linkit.scrap.domain.TeamScrap;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Slf4j
public class TeamScrapCustomRepositoryImpl implements TeamScrapCustomRepository {

    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public List<TeamScrap> findAllByMemberId(final Long memberId) {

        QTeamScrap qTeamScrap = QTeamScrap.teamScrap;

        return jpaQueryFactory
                .selectFrom(qTeamScrap)
                .where(qTeamScrap.member.id.eq(memberId))
                .fetch();

    }

    @Override
    public Optional<TeamScrap> findByTeamId(final Long teamId) {

        QTeamScrap qTeamScrap = QTeamScrap.teamScrap;

        TeamScrap result = jpaQueryFactory
                .selectFrom(qTeamScrap)
                .where(qTeamScrap.team.id.eq(teamId))
                .fetchOne();

        return Optional.ofNullable(result);

    }

    @Override
    public Optional<TeamScrap> findByMemberIdAndTeamId(final Long memberId, final Long teamId) {

        QTeamScrap qTeamScrap = QTeamScrap.teamScrap;

        TeamScrap result = jpaQueryFactory
                .selectFrom(qTeamScrap)
                .where(qTeamScrap.team.id.eq(teamId)
                        .and(qTeamScrap.member.id.eq(memberId)))
                .fetchOne();

        return Optional.ofNullable(result);

    }


    @Override
    public boolean existsByMemberId(Long memberId) {

        QTeamScrap qTeamScrap = QTeamScrap.teamScrap;

        Integer count = jpaQueryFactory
                .selectOne()
                .from(qTeamScrap)
                .where(qTeamScrap.member.id.eq(memberId))
                .fetchFirst();

        return count != null;

    }

    @Override
    @Transactional
    public void deleteByMemberId(final Long memberId) {

        QTeamScrap qTeamScrap = QTeamScrap.teamScrap;

        jpaQueryFactory
                .delete(qTeamScrap)
                .where(qTeamScrap.member.id.eq(memberId))
                .execute();
    }

    @Override
    @Transactional
    public void deleteByMemberIdAndTeamName(final Long memberId, final String teamName) {

        QTeamScrap qTeamScrap = QTeamScrap.teamScrap;

        jpaQueryFactory
                .delete(qTeamScrap)
                .where(qTeamScrap.member.id.eq(memberId)
                        .and(qTeamScrap.team.teamName.eq(teamName)))
                .execute();

    }

    @Override
    public boolean existsByMemberIdAndTeamName(final Long memberId, final String teamName) {

        QTeamScrap qTeamScrap = QTeamScrap.teamScrap;

        Integer count = jpaQueryFactory.selectOne().from(qTeamScrap)
                .where(qTeamScrap.member.id.eq(memberId)
                        .and(qTeamScrap.team.teamName.eq(teamName)))
                .fetchFirst();

        return count != null;
    }
}
