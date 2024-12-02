package liaison.linkit.team.domain.repository.announcement;

import liaison.linkit.team.domain.announcement.AnnouncementSkill;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AnnouncementSkillRepository extends JpaRepository<AnnouncementSkill, Long>, AnnouncementSkillCustomRepository {
}
