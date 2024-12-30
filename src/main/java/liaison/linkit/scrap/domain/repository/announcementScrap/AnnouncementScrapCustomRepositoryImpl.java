package liaison.linkit.scrap.domain.repository.announcementScrap;

import com.querydsl.jpa.impl.JPAQueryFactory;
import liaison.linkit.scrap.domain.QAnnouncementScrap;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RequiredArgsConstructor
@Slf4j
public class AnnouncementScrapCustomRepositoryImpl implements AnnouncementScrapCustomRepository {

    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public void deleteByMemberIdAndTeamMemberAnnouncementId(final Long memberId, final Long teamMemberAnnouncementId) {

        QAnnouncementScrap qAnnouncementScrap = QAnnouncementScrap.announcementScrap;

        jpaQueryFactory.delete(qAnnouncementScrap)
                .where(qAnnouncementScrap.member.id.eq(memberId)
                        .and(qAnnouncementScrap.teamMemberAnnouncement.id.eq(teamMemberAnnouncementId)))
                .execute();

    }

    @Override
    public boolean existsByMemberIdAndTeamMemberAnnouncementId(final Long memberId, final Long teamMemberAnnouncementId) {

        QAnnouncementScrap qAnnouncementScrap = QAnnouncementScrap.announcementScrap;

        Integer count = jpaQueryFactory
                .selectOne()
                .from(qAnnouncementScrap)
                .where(qAnnouncementScrap.member.id.eq(memberId)
                        .and(qAnnouncementScrap.teamMemberAnnouncement.id.eq(teamMemberAnnouncementId)))
                .fetchFirst();

        return count != null;

    }

    @Override
    public int getTotalAnnouncementScrapCount(final Long teamMemberAnnouncementId) {
        QAnnouncementScrap qAnnouncementScrap = QAnnouncementScrap.announcementScrap;

        Long count = jpaQueryFactory
                .select(qAnnouncementScrap.count())
                .from(qAnnouncementScrap)
                .where(qAnnouncementScrap.teamMemberAnnouncement.id.eq(teamMemberAnnouncementId))
                .fetchOne();

        // Long 값을 int로 변환 (null 체크 포함)
        return count != null ? count.intValue() : 0;
    }

}
