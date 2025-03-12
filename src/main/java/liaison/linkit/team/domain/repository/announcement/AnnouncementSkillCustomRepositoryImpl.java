package liaison.linkit.team.domain.repository.announcement;

import java.util.List;

import com.querydsl.jpa.impl.JPAQueryFactory;
import liaison.linkit.team.domain.announcement.AnnouncementSkill;
import liaison.linkit.team.domain.announcement.QAnnouncementSkill;
import liaison.linkit.team.domain.announcement.QTeamMemberAnnouncement;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class AnnouncementSkillCustomRepositoryImpl implements AnnouncementSkillCustomRepository {
    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public List<AnnouncementSkill> getAnnouncementSkills(final Long teamMemberAnnouncementId) {
        QAnnouncementSkill qAnnouncementSkill = QAnnouncementSkill.announcementSkill;
        QTeamMemberAnnouncement qTeamMemberAnnouncement =
                QTeamMemberAnnouncement.teamMemberAnnouncement;

        return jpaQueryFactory
                .selectFrom(qAnnouncementSkill)
                .join(qAnnouncementSkill.teamMemberAnnouncement, qTeamMemberAnnouncement)
                .where(qTeamMemberAnnouncement.id.eq(teamMemberAnnouncementId))
                .fetch();
    }

    @Override
    public boolean existsAnnouncementSkillsByTeamMemberAnnouncementId(
            final Long teamMemberAnnouncementId) {
        QAnnouncementSkill qAnnouncementSkill = QAnnouncementSkill.announcementSkill;

        // 최소 1개의 스킬이 존재하는지 확인
        return jpaQueryFactory
                        .selectOne()
                        .from(qAnnouncementSkill)
                        .where(
                                qAnnouncementSkill.teamMemberAnnouncement.id.eq(
                                        teamMemberAnnouncementId))
                        .fetchFirst()
                != null; // 존재하면 true, 없으면 false 반환
    }
}
