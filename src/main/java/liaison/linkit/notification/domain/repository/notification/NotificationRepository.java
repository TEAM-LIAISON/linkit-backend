package liaison.linkit.notification.domain.repository.notification;

import java.time.LocalDateTime;
import java.util.List;

import liaison.linkit.notification.domain.Notification;
import liaison.linkit.notification.domain.type.NotificationType;
import liaison.linkit.notification.domain.type.SubNotificationType;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

public interface NotificationRepository extends MongoRepository<Notification, String> {
    // 단일 알림 조회 (가장 최근 알림)
    @Query(value = "{ 'receiver_member_id': ?0 }", sort = "{ 'createdAt': -1 }")
    Notification findTopByReceiverMemberIdOrderByCreatedAtDesc(Long memberId);

    // 특정 회원의 모든 알림 조회 (생성일 기준 내림차순 정렬)
    @Query(value = "{ 'receiver_member_id': ?0 }", sort = "{ 'createdAt': -1 }")
    List<Notification> findAllByReceiverMemberIdOrderByCreatedAtDesc(Long memberId);

    // 수동 쿼리 + count
    @Query(
            value = "{ 'receiver_member_id': ?0, 'notification_read_status': 'UNREAD' }",
            count = true)
    long countUnreadMessages(Long memberId);

    // 특정 회원의 모든 알림 삭제
    @Query(value = "{ 'receiver_member_id': ?0 }", delete = true)
    long deleteAllByReceiverMemberId(Long memberId);

    // 인증 알림 존재 여부 확인 (타입 및 항목 ID 기준)
    @Query(
            value =
                    "{ 'notification_type': ?0, 'sub_notification_type': ?1, 'certificationDetails.itemId': ?2, 'certificationDetails.itemType': ?3}",
            exists = true)
    boolean existsByCertificationTypeAndItemId(
            NotificationType notificationType,
            SubNotificationType subNotificationType,
            Long itemId,
            String itemType);

    @Query(
            value =
                    "{ 'receiver_member_id': ?0, 'notification_type': ?1, 'sub_notification_type': ?2, 'createdAt': { $gte: ?3 } }",
            exists = true)
    boolean existsByReceiverMemberIdAndNotificationTypeAndCreatedAtAfterOneWeekAgo(
            Long memberId,
            NotificationType notificationType,
            SubNotificationType subNotificationType,
            LocalDateTime createdAt);
}
