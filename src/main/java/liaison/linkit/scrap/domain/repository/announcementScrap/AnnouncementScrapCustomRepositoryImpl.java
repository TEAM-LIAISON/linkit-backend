package liaison.linkit.scrap.domain.repository.announcementScrap;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import com.querydsl.jpa.impl.JPAQueryFactory;
import liaison.linkit.scrap.domain.AnnouncementScrap;
import liaison.linkit.scrap.domain.QAnnouncementScrap;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RequiredArgsConstructor
@Slf4j
public class AnnouncementScrapCustomRepositoryImpl implements AnnouncementScrapCustomRepository {

    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public void deleteByMemberIdAndTeamMemberAnnouncementId(
            final Long memberId, final Long teamMemberAnnouncementId) {

        QAnnouncementScrap qAnnouncementScrap = QAnnouncementScrap.announcementScrap;

        jpaQueryFactory
                .delete(qAnnouncementScrap)
                .where(
                        qAnnouncementScrap
                                .member
                                .id
                                .eq(memberId)
                                .and(
                                        qAnnouncementScrap.teamMemberAnnouncement.id.eq(
                                                teamMemberAnnouncementId)))
                .execute();
    }

    @Override
    public boolean existsByMemberIdAndTeamMemberAnnouncementId(
            final Long memberId, final Long teamMemberAnnouncementId) {

        QAnnouncementScrap qAnnouncementScrap = QAnnouncementScrap.announcementScrap;

        Integer count =
                jpaQueryFactory
                        .selectOne()
                        .from(qAnnouncementScrap)
                        .where(
                                qAnnouncementScrap
                                        .member
                                        .id
                                        .eq(memberId)
                                        .and(
                                                qAnnouncementScrap.teamMemberAnnouncement.id.eq(
                                                        teamMemberAnnouncementId)))
                        .fetchFirst();

        return count != null;
    }

    @Override
    public int getTotalAnnouncementScrapCount(final Long teamMemberAnnouncementId) {
        QAnnouncementScrap qAnnouncementScrap = QAnnouncementScrap.announcementScrap;

        Long count =
                jpaQueryFactory
                        .select(qAnnouncementScrap.count())
                        .from(qAnnouncementScrap)
                        .where(
                                qAnnouncementScrap.teamMemberAnnouncement.id.eq(
                                        teamMemberAnnouncementId))
                        .fetchOne();

        // Long 값을 int로 변환 (null 체크 포함)
        return count != null ? count.intValue() : 0;
    }

    @Override
    public List<AnnouncementScrap> findAllByMemberId(final Long memberId) {
        QAnnouncementScrap qAnnouncementScrap = QAnnouncementScrap.announcementScrap;

        return jpaQueryFactory
                .selectFrom(qAnnouncementScrap)
                .where(qAnnouncementScrap.member.id.eq(memberId))
                .fetch();
    }

    @Override
    public void deleteAllByMemberId(final Long memberId) {
        QAnnouncementScrap qAnnouncementScrap = QAnnouncementScrap.announcementScrap;

        long deletedCount =
                jpaQueryFactory
                        .delete(qAnnouncementScrap)
                        .where(qAnnouncementScrap.member.id.eq(memberId))
                        .execute();
    }

    @Override
    public void deleteAllByAnnouncementIds(final List<Long> teamMemberAnnouncementIds) {
        QAnnouncementScrap qAnnouncementScrap = QAnnouncementScrap.announcementScrap;

        long deletedCount =
                jpaQueryFactory
                        .delete(qAnnouncementScrap)
                        .where(
                                qAnnouncementScrap.teamMemberAnnouncement.id.in(
                                        teamMemberAnnouncementIds))
                        .execute();
    }

    @Override
    public Set<Long> findScrappedAnnouncementIdsByMember(
            Long memberId, List<Long> announcementIds) {
        QAnnouncementScrap qAnnouncementScrap = QAnnouncementScrap.announcementScrap;

        if (memberId == null || announcementIds == null || announcementIds.isEmpty()) {
            return Set.of();
        }

        return new HashSet<>(
                jpaQueryFactory
                        .select(qAnnouncementScrap.teamMemberAnnouncement.id)
                        .from(qAnnouncementScrap)
                        .where(
                                qAnnouncementScrap.member.id.eq(memberId),
                                qAnnouncementScrap.teamMemberAnnouncement.id.in(announcementIds))
                        .fetch());
    }

    @Override
    public Map<Long, Integer> countScrapsGroupedByAnnouncement(List<Long> announcementIds) {
        QAnnouncementScrap qAnnouncementScrap = QAnnouncementScrap.announcementScrap;

        return jpaQueryFactory
                .select(qAnnouncementScrap.teamMemberAnnouncement.id, qAnnouncementScrap.count())
                .from(qAnnouncementScrap)
                .where(qAnnouncementScrap.teamMemberAnnouncement.id.in(announcementIds))
                .groupBy(qAnnouncementScrap.teamMemberAnnouncement.id)
                .fetch()
                .stream()
                .filter(tuple -> tuple.get(qAnnouncementScrap.teamMemberAnnouncement.id) != null)
                .collect(
                        Collectors.toMap(
                                tuple -> tuple.get(qAnnouncementScrap.teamMemberAnnouncement.id),
                                tuple -> {
                                    Long count = tuple.get(qAnnouncementScrap.count());
                                    return count != null ? count.intValue() : 0;
                                }));
    }
}
