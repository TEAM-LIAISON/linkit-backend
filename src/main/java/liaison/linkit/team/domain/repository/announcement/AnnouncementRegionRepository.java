package liaison.linkit.team.domain.repository.announcement;

import liaison.linkit.team.domain.announcement.AnnouncementRegion;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AnnouncementRegionRepository extends JpaRepository<AnnouncementRegion, Long>, AnnouncementRegionCustomRepository {
    
}
