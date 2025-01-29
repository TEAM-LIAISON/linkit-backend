package liaison.linkit.scrap.domain.repository.teamScrap;

import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.List;
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
    public void deleteByMemberIdAndTeamCode(final Long memberId, final String teamCode) {

        QTeamScrap qTeamScrap = QTeamScrap.teamScrap;

        jpaQueryFactory
                .delete(qTeamScrap)
                .where(qTeamScrap.member.id.eq(memberId)
                        .and(qTeamScrap.team.teamCode.eq(teamCode)))
                .execute();

    }

    @Override
    public boolean existsByMemberIdAndTeamCode(final Long memberId, final String teamCode) {
        QTeamScrap qTeamScrap = QTeamScrap.teamScrap;

        Integer count = jpaQueryFactory.selectOne()
                .from(qTeamScrap)
                .where(qTeamScrap.member.id.eq(memberId)
                        .and(qTeamScrap.team.teamCode.eq(teamCode)))
                .fetchFirst();

        return count != null;
    }

    @Override
    public int countTotalTeamScrapByTeamCode(final String teamCode) {
        QTeamScrap qTeamScrap = QTeamScrap.teamScrap;

        Long count = jpaQueryFactory
                .select(qTeamScrap.count())
                .from(qTeamScrap)
                .where(qTeamScrap.team.teamCode.eq(teamCode))
                .fetchOne();

        return count != null ? count.intValue() : 0;
    }

    @Override
    public void deleteAllByMemberId(final Long memberId) {
        QTeamScrap qTeamScrap = QTeamScrap.teamScrap;

        long deletedCount = jpaQueryFactory
                .delete(qTeamScrap)
                .where(qTeamScrap.member.id.eq(memberId))
                .execute();

        log.info("Deleted {} team scraps for memberId: {}", deletedCount, memberId);
    }

    @Override
    public void deleteAllByTeamIds(final List<Long> teamIds) {
        QTeamScrap qTeamScrap = QTeamScrap.teamScrap;

        long deletedCount = jpaQueryFactory
                .delete(qTeamScrap)
                .where(qTeamScrap.team.id.in(teamIds))
                .execute();

        log.info("Deleted {} team scraps for teamIds: {}", deletedCount, teamIds);
    }

    @Override
    public void deleteAllByTeamId(final Long teamId) {
        QTeamScrap qTeamScrap = QTeamScrap.teamScrap;

        long deletedCount = jpaQueryFactory
                .delete(qTeamScrap)
                .where(qTeamScrap.team.id.eq(teamId))
                .execute();

        log.info("Deleted {} team scraps for teamId: {}", deletedCount, teamId);

    }
}
