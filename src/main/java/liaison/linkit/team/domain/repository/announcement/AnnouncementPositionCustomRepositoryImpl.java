package liaison.linkit.team.domain.repository.announcement;

import java.util.Optional;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

import com.querydsl.jpa.impl.JPAQueryFactory;
import liaison.linkit.team.domain.announcement.AnnouncementPosition;
import liaison.linkit.team.domain.announcement.QAnnouncementPosition;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

@Slf4j
@Repository
@RequiredArgsConstructor
public class AnnouncementPositionCustomRepositoryImpl
        implements AnnouncementPositionCustomRepository {

    private final JPAQueryFactory jpaQueryFactory;

    @PersistenceContext private EntityManager entityManager; // EntityManager 주입

    @Override
    public boolean existsAnnouncementPositionByTeamMemberAnnouncementId(
            final Long teamMemberAnnouncementId) {
        QAnnouncementPosition qAnnouncementPosition = QAnnouncementPosition.announcementPosition;

        Integer count =
                jpaQueryFactory
                        .selectOne()
                        .from(qAnnouncementPosition)
                        .where(
                                qAnnouncementPosition.teamMemberAnnouncement.id.eq(
                                        teamMemberAnnouncementId))
                        .fetchFirst();

        return count != null;
    }

    @Override
    public Optional<AnnouncementPosition> findAnnouncementPositionByTeamMemberAnnouncementId(
            final Long teamMemberAnnouncementId) {
        QAnnouncementPosition qAnnouncementPosition = QAnnouncementPosition.announcementPosition;

        AnnouncementPosition announcementPosition =
                jpaQueryFactory
                        .selectFrom(qAnnouncementPosition)
                        .where(
                                qAnnouncementPosition.teamMemberAnnouncement.id.eq(
                                        teamMemberAnnouncementId))
                        .fetchOne();

        return Optional.ofNullable(announcementPosition);
    }

    @Override
    public void deleteAllByTeamMemberAnnouncementId(final Long teamMemberAnnouncementId) {
        QAnnouncementPosition qAnnouncementPosition = QAnnouncementPosition.announcementPosition;

        jpaQueryFactory
                .delete(qAnnouncementPosition)
                .where(qAnnouncementPosition.teamMemberAnnouncement.id.eq(teamMemberAnnouncementId))
                .execute();

        entityManager.flush();
        entityManager.clear();
    }
}
