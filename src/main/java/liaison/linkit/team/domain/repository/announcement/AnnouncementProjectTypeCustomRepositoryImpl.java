package liaison.linkit.team.domain.repository.announcement;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

import com.querydsl.jpa.impl.JPAQueryFactory;
import liaison.linkit.team.domain.announcement.QAnnouncementProjectType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

@Slf4j
@Repository
@RequiredArgsConstructor
public class AnnouncementProjectTypeCustomRepositoryImpl
        implements AnnouncementProjectTypeCustomRepository {

    private final JPAQueryFactory jpaQueryFactory;

    @PersistenceContext private EntityManager entityManager;

    @Override
    public boolean existsAnnouncementProjectTypeByTeamMemberAnnouncementId(
            final Long teamMemberAnnouncementId) {
        QAnnouncementProjectType qAnnouncementProjectType =
                QAnnouncementProjectType.announcementProjectType;

        Integer count =
                jpaQueryFactory
                        .selectOne()
                        .from(qAnnouncementProjectType)
                        .where(
                                qAnnouncementProjectType.teamMemberAnnouncement.id.eq(
                                        teamMemberAnnouncementId))
                        .fetchFirst();

        return count != null;
    }

    @Override
    public void deleteByTeamMemberAnnouncementId(final Long teamMemberAnnouncementId) {
        QAnnouncementProjectType qAnnouncementProjectType =
                QAnnouncementProjectType.announcementProjectType;

        jpaQueryFactory
                .delete(qAnnouncementProjectType)
                .where(
                        qAnnouncementProjectType.teamMemberAnnouncement.id.eq(
                                teamMemberAnnouncementId))
                .execute();

        entityManager.flush();
        entityManager.clear();
    }
}
