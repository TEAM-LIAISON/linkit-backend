package liaison.linkit.team.domain.repository.announcement;

import liaison.linkit.team.domain.announcement.AnnouncementAdvertisingLog;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface AnnouncementAdvertisingLogRepository
        extends MongoRepository<AnnouncementAdvertisingLog, String> {

    boolean existsByTeamMemberAnnouncementId(Long announcementId);
}
