package liaison.linkit.team.domain.repository.announcement;

import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import java.util.List;
import liaison.linkit.team.domain.announcement.QTeamMemberAnnouncement;
import liaison.linkit.team.domain.announcement.TeamMemberAnnouncement;
import liaison.linkit.team.presentation.announcement.dto.TeamMemberAnnouncementRequestDTO.UpdateTeamMemberAnnouncementRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class TeamMemberAnnouncementCustomRepositoryImpl implements TeamMemberAnnouncementCustomRepository {

    private final JPAQueryFactory jpaQueryFactory;

    @PersistenceContext
    private EntityManager entityManager; // EntityManager 주입

    @Override
    public List<TeamMemberAnnouncement> getTeamMemberAnnouncements(final Long teamId) {
        QTeamMemberAnnouncement qTeamMemberAnnouncement = QTeamMemberAnnouncement.teamMemberAnnouncement;

        return jpaQueryFactory
                .selectFrom(qTeamMemberAnnouncement)
                .where(qTeamMemberAnnouncement.team.id.eq(teamId))
                .fetch();
    }

    @Override
    public TeamMemberAnnouncement getTeamMemberAnnouncement(final Long teamMemberAnnouncementId) {
        QTeamMemberAnnouncement qTeamMemberAnnouncement = QTeamMemberAnnouncement.teamMemberAnnouncement;

        return jpaQueryFactory
                .selectFrom(qTeamMemberAnnouncement)
                .where(qTeamMemberAnnouncement.id.eq(teamMemberAnnouncementId))
                .fetchOne();
    }

    @Override
    public TeamMemberAnnouncement updateTeamMemberAnnouncement(final TeamMemberAnnouncement teamMemberAnnouncement, final UpdateTeamMemberAnnouncementRequest request) {
        QTeamMemberAnnouncement qTeamMemberAnnouncement = QTeamMemberAnnouncement.teamMemberAnnouncement;

        // 팀원 공고 업데이트
        long updatedCount = jpaQueryFactory
                .update(qTeamMemberAnnouncement)
                .set(qTeamMemberAnnouncement.announcementTitle, request.getAnnouncementTitle())
                .set(qTeamMemberAnnouncement.announcementStartDate, request.getAnnouncementStartDate())
                .set(qTeamMemberAnnouncement.announcementEndDate, request.getAnnouncementEndDate())
                .set(qTeamMemberAnnouncement.isRegionFlexible, request.getIsRegionFlexible())
                .set(qTeamMemberAnnouncement.mainTasks, request.getMainTasks())
                .set(qTeamMemberAnnouncement.workMethod, request.getMainTasks())
                .set(qTeamMemberAnnouncement.idealCandidate, request.getPreferredQualifications())
                .set(qTeamMemberAnnouncement.preferredQualifications, request.getPreferredQualifications())
                .set(qTeamMemberAnnouncement.joiningProcess, request.getJoiningProcess())
                .set(qTeamMemberAnnouncement.benefits, request.getBenefits())
                .where(qTeamMemberAnnouncement.id.eq(teamMemberAnnouncement.getId()))
                .execute();

        entityManager.flush();
        entityManager.clear();

        if (updatedCount > 0) {
            return jpaQueryFactory
                    .selectFrom(qTeamMemberAnnouncement)
                    .where(qTeamMemberAnnouncement.id.eq(teamMemberAnnouncement.getId()))
                    .fetchOne();
        } else {
            return null;
        }
    }

    @Override
    public TeamMemberAnnouncement updateTeamMemberAnnouncementPublicState(final TeamMemberAnnouncement teamMemberAnnouncement, final boolean isTeamMemberAnnouncementCurrentPublicState) {
        QTeamMemberAnnouncement qTeamMemberAnnouncement = QTeamMemberAnnouncement.teamMemberAnnouncement;

        // QueryDSL을 사용하여 데이터베이스에서 ProfileLog 엔티티를 업데이트
        long updatedCount = jpaQueryFactory
                .update(qTeamMemberAnnouncement)
                .set(qTeamMemberAnnouncement.isAnnouncementPublic, !isTeamMemberAnnouncementCurrentPublicState)
                .where(qTeamMemberAnnouncement.id.eq(teamMemberAnnouncement.getId()))
                .execute();

        entityManager.flush();
        entityManager.clear();

        if (updatedCount > 0) { // 업데이트 성공 확인
            teamMemberAnnouncement.setIsAnnouncementPublic(!isTeamMemberAnnouncementCurrentPublicState); // 메모리 내 객체 업데이트
            return teamMemberAnnouncement;
        } else {
            throw new IllegalStateException("팀원 공고 공개/비공개 업데이트 실패");
        }
    }
}
