package liaison.linkit.team.implement.announcement;

import java.util.List;
import liaison.linkit.common.annotation.Adapter;
import liaison.linkit.team.domain.announcement.AnnouncementSkill;
import liaison.linkit.team.domain.repository.announcement.AnnouncementSkillRepository;
import lombok.RequiredArgsConstructor;

@Adapter
@RequiredArgsConstructor
public class AnnouncementSkillQueryAdapter {
    private final AnnouncementSkillRepository announcementSkillRepository;

    public List<AnnouncementSkill> getAnnouncementSkills(final Long teamMemberAnnouncementId) {
        return announcementSkillRepository.getAnnouncementSkills(teamMemberAnnouncementId);
    }
}
