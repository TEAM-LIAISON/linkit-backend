package liaison.linkit.team.domain.repository.announcement;

import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.List;
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
        QTeamMemberAnnouncement qTeamMemberAnnouncement = QTeamMemberAnnouncement.teamMemberAnnouncement;

        return jpaQueryFactory
                .selectFrom(qAnnouncementSkill)
                .join(qAnnouncementSkill.teamMemberAnnouncement, qTeamMemberAnnouncement)
                .where(qTeamMemberAnnouncement.id.eq(teamMemberAnnouncementId))
                .fetch();
    }
}
