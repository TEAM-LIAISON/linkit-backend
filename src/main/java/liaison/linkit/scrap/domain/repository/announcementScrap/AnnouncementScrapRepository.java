package liaison.linkit.scrap.domain.repository.announcementScrap;

import liaison.linkit.scrap.domain.AnnouncementScrap;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AnnouncementScrapRepository extends JpaRepository<AnnouncementScrap, Long>, AnnouncementScrapCustomRepository {
}
