package liaison.linkit.team.domain.repository.announcement;

import java.util.List;
import liaison.linkit.team.domain.announcement.AnnouncementSkill;

public interface AnnouncementSkillCustomRepository {
    List<AnnouncementSkill> getAnnouncementSkills(final Long teamMemberAnnouncementId);

    boolean existsAnnouncementSkillsByTeamMemberAnnouncementId(final Long teamMemberAnnouncementId);
}
