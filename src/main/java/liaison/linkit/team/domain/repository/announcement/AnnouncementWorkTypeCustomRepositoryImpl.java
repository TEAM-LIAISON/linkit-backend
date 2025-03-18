package liaison.linkit.team.domain.repository.announcement;

import java.util.Optional;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

import com.querydsl.jpa.impl.JPAQueryFactory;
import liaison.linkit.team.domain.announcement.AnnouncementWorkType;
import liaison.linkit.team.domain.announcement.QAnnouncementWorkType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

@Slf4j
@Repository
@RequiredArgsConstructor
public class AnnouncementWorkTypeCustomRepositoryImpl
        implements AnnouncementWorkTypeCustomRepository {

    private final JPAQueryFactory jpaQueryFactory;

    @PersistenceContext private EntityManager entityManager;

    @Override
    public boolean existsAnnouncementWorkTypeByTeamMemberAnnouncementId(
            final Long teamMemberAnnouncementId) {
        QAnnouncementWorkType qAnnouncementWorkType = QAnnouncementWorkType.announcementWorkType;

        Integer count =
                jpaQueryFactory
                        .selectOne()
                        .from(qAnnouncementWorkType)
                        .where(
                                qAnnouncementWorkType.teamMemberAnnouncement.id.eq(
                                        teamMemberAnnouncementId))
                        .fetchFirst();

        return count != null;
    }

    @Override
    public Optional<AnnouncementWorkType> findAnnouncementWorkTypeByTeamMemberAnnouncementId(
            final Long teamMemberAnnouncementId) {
        QAnnouncementWorkType qAnnouncementWorkType = QAnnouncementWorkType.announcementWorkType;

        AnnouncementWorkType announcementWorkType =
                jpaQueryFactory
                        .selectFrom(qAnnouncementWorkType)
                        .where(
                                qAnnouncementWorkType.teamMemberAnnouncement.id.eq(
                                        teamMemberAnnouncementId))
                        .fetchFirst();

        return Optional.ofNullable(announcementWorkType);
    }

    @Override
    public void deleteByTeamMemberAnnouncementId(final Long teamMemberAnnouncementId) {
        QAnnouncementWorkType qAnnouncementWorkType = QAnnouncementWorkType.announcementWorkType;

        jpaQueryFactory
                .delete(qAnnouncementWorkType)
                .where(qAnnouncementWorkType.teamMemberAnnouncement.id.eq(teamMemberAnnouncementId))
                .execute();

        entityManager.flush();
        entityManager.clear();
    }
}
