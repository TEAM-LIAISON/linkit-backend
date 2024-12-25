package liaison.linkit.notification.domain.repository.notification;

import liaison.linkit.notification.domain.Notification;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface NotificationRepository extends MongoRepository<Notification, String> {
}
