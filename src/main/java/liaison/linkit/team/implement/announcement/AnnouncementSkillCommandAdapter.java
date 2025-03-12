package liaison.linkit.team.implement.announcement;

import java.util.List;

import liaison.linkit.common.annotation.Adapter;
import liaison.linkit.team.domain.announcement.AnnouncementSkill;
import liaison.linkit.team.domain.repository.announcement.AnnouncementSkillRepository;
import lombok.RequiredArgsConstructor;

@Adapter
@RequiredArgsConstructor
public class AnnouncementSkillCommandAdapter {
    private final AnnouncementSkillRepository announcementSkillRepository;

    public void saveAll(final List<AnnouncementSkill> announcementSkills) {
        announcementSkillRepository.saveAll(announcementSkills);
    }

    public void deleteAll(final List<AnnouncementSkill> announcementSkills) {
        announcementSkillRepository.deleteAll(announcementSkills);
    }
}
