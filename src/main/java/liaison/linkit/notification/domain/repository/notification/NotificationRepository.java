package liaison.linkit.notification.domain.repository.notification;

import java.util.List;
import liaison.linkit.notification.domain.Notification;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface NotificationRepository extends MongoRepository<Notification, String> {
    Notification getNotificationByMemberId(final String memberId);

    List<Notification> getNotificationsByMemberId(String memberId);
}
