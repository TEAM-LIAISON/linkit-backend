package liaison.linkit.notification.domain.repository.notification;

import java.util.List;
import liaison.linkit.notification.domain.Notification;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

public interface NotificationRepository extends MongoRepository<Notification, String> {
    Notification getNotificationByReceiverMemberId(final Long memberId);

    List<Notification> getNotificationsByReceiverMemberId(final Long memberId);

    // 수동 쿼리 + count
    @Query(value = "{ 'receiver_member_id': ?0, 'notification_read_status': 'UNREAD' }", count = true)
    long countUnreadMessages(Long memberId);
}
