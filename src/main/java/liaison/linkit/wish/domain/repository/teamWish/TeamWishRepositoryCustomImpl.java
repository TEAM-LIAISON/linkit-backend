package liaison.linkit.wish.domain.repository.teamWish;

import com.querydsl.jpa.impl.JPAQueryFactory;
import liaison.linkit.global.type.StatusType;
import liaison.linkit.wish.domain.QTeamWish;
import liaison.linkit.wish.domain.TeamWish;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Slf4j
public class TeamWishRepositoryCustomImpl implements TeamWishRepositoryCustom{

    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public List<TeamWish> findAllByMemberId(final Long memberId) {
        QTeamWish teamWish = QTeamWish.teamWish;
        return jpaQueryFactory
                .selectFrom(teamWish)
                .where(teamWish.member.id.eq(memberId))
                .fetch();
    }

    @Override
    public Optional<TeamWish> findByTeamMemberAnnouncementId(Long teamMemberAnnouncementId) {
        QTeamWish teamWish = QTeamWish.teamWish;
        TeamWish result = jpaQueryFactory
                .selectFrom(teamWish)
                .where(teamWish.teamMemberAnnouncement.id.eq(teamMemberAnnouncementId))
                .fetchOne();
        return Optional.ofNullable(result);
    }

    @Override
    public Optional<TeamWish> findByMemberIdAndTeamMemberAnnouncementId(Long teamMemberAnnouncementId, Long memberId) {
        QTeamWish teamWish = QTeamWish.teamWish;
        TeamWish result = jpaQueryFactory
                .selectFrom(teamWish)
                .where(teamWish.teamMemberAnnouncement.id.eq(teamMemberAnnouncementId)
                        .and(teamWish.member.id.eq(memberId)))
                .fetchOne();
        return Optional.ofNullable(result);
    }

    @Override
    public boolean findByTeamMemberAnnouncementIdAndMemberId(final Long teamMemberAnnouncementId, final Long memberId) {
        QTeamWish teamWish = QTeamWish.teamWish;
        Integer count = jpaQueryFactory
                .selectOne()
                .from(teamWish)
                .where(teamWish.teamMemberAnnouncement.id.eq(teamMemberAnnouncementId)
                        .and(teamWish.member.id.eq(memberId)))
                .fetchFirst();
        return count != null;
    }

    @Override
    public boolean existsByMemberId(Long memberId) {
        QTeamWish teamWish = QTeamWish.teamWish;
        Integer count = jpaQueryFactory
                .selectOne()
                .from(teamWish)
                .where(teamWish.member.id.eq(memberId))
                .fetchFirst();
        return count != null;
    }

    @Override
    public boolean existsByTeamMemberAnnouncementId(final Long teamMemberAnnouncementId) {
        QTeamWish teamWish = QTeamWish.teamWish;
        Integer count = jpaQueryFactory
                .selectOne()
                .from(teamWish)
                .where(teamWish.teamMemberAnnouncement.id.eq(teamMemberAnnouncementId))
                .fetchFirst();
        return count != null;
    }

    @Override
    public boolean existsByTeamMemberAnnouncementIds(final List<Long> teamMemberAnnouncementIds) {
        QTeamWish teamWish = QTeamWish.teamWish;
        Integer count = jpaQueryFactory
                .selectOne()
                .from(teamWish)
                .where(teamWish.teamMemberAnnouncement.id.in(teamMemberAnnouncementIds))
                .fetchFirst();
        return count != null;
    }

    @Override
    @Transactional
    public void deleteByMemberId(final Long memberId) {
        QTeamWish teamWish = QTeamWish.teamWish;
        jpaQueryFactory
                .update(teamWish)
                .set(teamWish.status, StatusType.DELETED)
                .where(teamWish.member.id.eq(memberId))
                .execute();
    }

    @Override
    @Transactional
    public void deleteByMemberIdAndTeamMemberAnnouncementId(final Long memberId, final Long teamMemberAnnouncementId) {
        QTeamWish teamWish = QTeamWish.teamWish;
        jpaQueryFactory
                .delete(teamWish)
                .where(teamWish.member.id.eq(memberId)
                        .and(teamWish.teamMemberAnnouncement.id.eq(teamMemberAnnouncementId)))
                .execute();
    }

    @Override
    @Transactional
    public void deleteByTeamMemberAnnouncementId(final Long teamMemberAnnouncementId) {
        QTeamWish teamWish = QTeamWish.teamWish;
        jpaQueryFactory
                .update(teamWish)
                .set(teamWish.status, StatusType.DELETED)
                .where(teamWish.teamMemberAnnouncement.id.eq(teamMemberAnnouncementId))
                .execute();
    }

    @Override
    @Transactional
    public void deleteByTeamMemberAnnouncementIds(final List<Long> teamMemberAnnouncementIds) {
        QTeamWish teamWish = QTeamWish.teamWish;
        jpaQueryFactory
                .update(teamWish)
                .set(teamWish.status, StatusType.DELETED)
                .where(teamWish.teamMemberAnnouncement.id.in(teamMemberAnnouncementIds))
                .execute();
    }
}
