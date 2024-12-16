package liaison.linkit.scrap.domain.repository.announcementScrap;

import com.querydsl.jpa.impl.JPAQueryFactory;
import liaison.linkit.scrap.domain.QTeamMemberAnnouncementScrap;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RequiredArgsConstructor
@Slf4j
public class AnnouncementScrapCustomRepositoryImpl implements AnnouncementScrapCustomRepository {

    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public void deleteByMemberIdAndTeamMemberAnnouncementId(final Long memberId, final Long teamMemberAnnouncementId) {

        QTeamMemberAnnouncementScrap qTeamMemberAnnouncementScrap = QTeamMemberAnnouncementScrap.teamMemberAnnouncementScrap;

        jpaQueryFactory.delete(qTeamMemberAnnouncementScrap)
                .where(qTeamMemberAnnouncementScrap.member.id.eq(memberId)
                        .and(qTeamMemberAnnouncementScrap.teamMemberAnnouncement.id.eq(teamMemberAnnouncementId)))
                .execute();

    }

    @Override
    public boolean existsByMemberIdAndTeamMemberAnnouncementId(final Long memberId, final Long teamMemberAnnouncementId) {

        QTeamMemberAnnouncementScrap qTeamMemberAnnouncementScrap = QTeamMemberAnnouncementScrap.teamMemberAnnouncementScrap;

        Integer count = jpaQueryFactory
                .selectOne()
                .from(qTeamMemberAnnouncementScrap)
                .where(qTeamMemberAnnouncementScrap.member.id.eq(memberId)
                        .and(qTeamMemberAnnouncementScrap.teamMemberAnnouncement.id.eq(teamMemberAnnouncementId)))
                .fetchFirst();

        return count != null;

    }
}
