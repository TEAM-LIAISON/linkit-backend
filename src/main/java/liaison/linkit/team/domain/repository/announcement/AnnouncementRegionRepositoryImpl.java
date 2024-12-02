package liaison.linkit.team.domain.repository.announcement;

import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.Optional;
import liaison.linkit.team.domain.announcement.AnnouncementRegion;
import liaison.linkit.team.domain.announcement.QAnnouncementRegion;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class AnnouncementRegionRepositoryImpl implements AnnouncementRegionCustomRepository {
    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public Optional<AnnouncementRegion> findAnnouncementRegionByTeamMemberAnnouncementId(final Long teamMemberAnnouncementId) {
        QAnnouncementRegion qAnnouncementRegion = QAnnouncementRegion.announcementRegion;

        AnnouncementRegion announcementRegion = jpaQueryFactory
                .selectFrom(qAnnouncementRegion)
                .where(
                        qAnnouncementRegion.teamMemberAnnouncement.id.eq(teamMemberAnnouncementId)
                ).fetchOne();

        return Optional.ofNullable(announcementRegion);
    }

    @Override
    public boolean existsAnnouncementRegionByTeamMemberAnnouncementId(final Long teamMemberAnnouncementId) {
        QAnnouncementRegion qAnnouncementRegion = QAnnouncementRegion.announcementRegion;

        Integer count = jpaQueryFactory
                .selectOne()
                .from(qAnnouncementRegion)
                .where(qAnnouncementRegion.teamMemberAnnouncement.id.eq(teamMemberAnnouncementId))
                .fetchFirst();

        return count != null;
    }

    @Override
    public void deleteByTeamMemberAnnouncementId(Long teamMemberAnnouncementId) {
        QAnnouncementRegion qAnnouncementRegion = QAnnouncementRegion.announcementRegion;

        jpaQueryFactory
                .delete(qAnnouncementRegion)
                .where(qAnnouncementRegion.teamMemberAnnouncement.id.eq(teamMemberAnnouncementId))
                .execute();
    }
}
