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
    public boolean existsByTeamIdAndMemberId(final Long teamId, final Long memberId) {

        QTeamScrap qTeamScrap = QTeamScrap.teamScrap;

        Integer count = jpaQueryFactory
                .selectOne()
                .from(qTeamScrap)
                .where(qTeamScrap.team.id.eq(teamId)
                        .and(qTeamScrap.member.id.eq(memberId)))
                .fetchFirst();

        return count != null;

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
    public boolean existsByTeamId(final Long teamId) {

        QTeamScrap qTeamScrap = QTeamScrap.teamScrap;

        Integer count = jpaQueryFactory
                .selectOne()
                .from(qTeamScrap)
                .where(qTeamScrap.team.id.eq(teamId))
                .fetchFirst();

        return count != null;

    }

    @Override
    public boolean existsByTeamIds(final List<Long> teamIds) {

        QTeamScrap qTeamScrap = QTeamScrap.teamScrap;

        Integer count = jpaQueryFactory
                .selectOne()
                .from(qTeamScrap)
                .where(qTeamScrap.team.id.in(teamIds))
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
    public void deleteByMemberIdAndTeamId(final Long memberId, final Long teamId) {

        QTeamScrap qTeamScrap = QTeamScrap.teamScrap;

        jpaQueryFactory
                .delete(qTeamScrap)
                .where(qTeamScrap.member.id.eq(memberId)
                        .and(qTeamScrap.team.id.eq(teamId)))
                .execute();
    }

    @Override
    @Transactional
    public void deleteByTeamId(final Long teamId) {

        QTeamScrap qTeamScrap = QTeamScrap.teamScrap;

        jpaQueryFactory
                .delete(qTeamScrap)
                .where(qTeamScrap.team.id.eq(teamId))
                .execute();
    }

    @Override
    @Transactional
    public void deleteByTeamIds(final List<Long> teamIds) {

        QTeamScrap qTeamScrap = QTeamScrap.teamScrap;

        jpaQueryFactory
                .delete(qTeamScrap)
                .where(qTeamScrap.team.id.in(teamIds))
                .execute();
    }

    @Override
    public boolean existsByMemberIdAndTeamId(final Long memberId, final Long teamId) {

        QTeamScrap qTeamScrap = QTeamScrap.teamScrap;

        Integer count = jpaQueryFactory.selectOne().from(qTeamScrap)
                .where(qTeamScrap.member.id.eq(memberId)
                        .and(qTeamScrap.team.id.eq(teamId)))
                .fetchFirst();

        return count != null;
    }
}
