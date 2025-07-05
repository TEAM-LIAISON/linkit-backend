package liaison.linkit.notification.business.handler;

import liaison.linkit.matching.business.resolver.MatchingInfoResolver;
import liaison.linkit.matching.domain.Matching;
import liaison.linkit.matching.domain.type.ReceiverType;
import liaison.linkit.notification.business.NotificationMapper;
import liaison.linkit.notification.domain.type.NotificationType;
import liaison.linkit.notification.domain.type.SubNotificationType;
import liaison.linkit.notification.presentation.dto.NotificationResponseDTO.NotificationDetails;
import liaison.linkit.notification.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class NotificationHandler {

    private final MatchingInfoResolver matchingInfoResolver;
    private final NotificationService notificationService;
    private final NotificationMapper notificationMapper;

    /** 수신자 매칭 요청 알림 생성 */
    public NotificationDetails generateRequestedStateReceiverNotificationDetails(
            final Matching matching) {
        if (matching.getReceiverType().equals(ReceiverType.ANNOUNCEMENT)) {
            return NotificationDetails.announcementRequested(
                    matching.getId(),
                    matchingInfoResolver.getSenderLogoImagePath(matching),
                    matchingInfoResolver.getSenderName(matching),
                    matchingInfoResolver.getReceiverMajorPosition(matching));
        } else {
            return NotificationDetails.matchingRequested(
                    matching.getId(),
                    matchingInfoResolver.getSenderLogoImagePath(matching),
                    matchingInfoResolver.getSenderName(matching));
        }
    }

    /** 수신자 매칭 성사 알림 생성 */
    public NotificationDetails generateAcceptedStateReceiverNotificationDetails(
            final Matching matching) {
        if (matching.getReceiverType().equals(ReceiverType.ANNOUNCEMENT)) {
            return NotificationDetails.announcementAccepted(
                    matching.getId(),
                    matchingInfoResolver.getSenderLogoImagePath(matching),
                    matchingInfoResolver.getSenderName(matching),
                    matchingInfoResolver.getReceiverMajorPosition(matching));
        } else {
            return NotificationDetails.matchingAccepted(
                    matching.getId(),
                    matchingInfoResolver.getSenderLogoImagePath(matching),
                    matchingInfoResolver.getSenderName(matching));
        }
    }

    /** 발신자 매칭 성사 알림 생성 */
    public NotificationDetails generateAcceptedStateSenderNotificationDetails(
            final Matching matching) {
        if (matching.getReceiverType().equals(ReceiverType.ANNOUNCEMENT)) {
            return NotificationDetails.announcementAccepted(
                    matching.getId(),
                    matchingInfoResolver.getReceiverLogoImagePath(matching),
                    matchingInfoResolver.getReceiverName(matching),
                    matchingInfoResolver.getReceiverMajorPosition(matching));
        } else {
            return NotificationDetails.matchingAccepted(
                    matching.getId(),
                    matchingInfoResolver.getReceiverLogoImagePath(matching),
                    matchingInfoResolver.getReceiverName(matching));
        }
    }

    /** 발신자 매칭 거절 알림 생성 */
    public NotificationDetails generatedRejectedStateSenderNotificationDetails(
            final Matching matching) {
        if (matching.getReceiverType().equals(ReceiverType.ANNOUNCEMENT)) {
            return NotificationDetails.announcementRejected(
                    matching.getId(),
                    matchingInfoResolver.getReceiverLogoImagePath(matching),
                    matchingInfoResolver.getReceiverName(matching),
                    matchingInfoResolver.getReceiverMajorPosition(matching));
        } else {
            return NotificationDetails.matchingRejected(
                    matching.getId(),
                    matchingInfoResolver.getReceiverLogoImagePath(matching),
                    matchingInfoResolver.getReceiverName(matching));
        }
    }

    /** 수신자에게 매칭 요청 알림 발송 */
    public void alertNewRequestedNotificationToReceiver(
            final Matching matching, final NotificationDetails notificationDetails) {
        if (matching.getReceiverType().equals(ReceiverType.ANNOUNCEMENT)) {
            notificationService.alertNewNotification(
                    notificationMapper.toNotification(
                            matchingInfoResolver.getReceiverMemberId(matching),
                            NotificationType.ANNOUNCEMENT,
                            SubNotificationType.ANNOUNCEMENT_REQUESTED,
                            notificationDetails));
        } else {
            notificationService.alertNewNotification(
                    notificationMapper.toNotification(
                            matchingInfoResolver.getReceiverMemberId(matching),
                            NotificationType.MATCHING,
                            SubNotificationType.MATCHING_REQUESTED,
                            notificationDetails));
        }
    }

    /** 수신자에게 매칭 성사 알림 발송 */
    public void alertNewAcceptedNotificationToReceiver(
            final Matching matching, final NotificationDetails notificationDetails) {
        if (matching.getReceiverType().equals(ReceiverType.ANNOUNCEMENT)) {
            notificationService.alertNewNotification(
                    notificationMapper.toNotification(
                            matchingInfoResolver.getReceiverMemberId(matching),
                            NotificationType.ANNOUNCEMENT,
                            SubNotificationType.ANNOUNCEMENT_ACCEPTED,
                            notificationDetails));
        } else {
            notificationService.alertNewNotification(
                    notificationMapper.toNotification(
                            matchingInfoResolver.getReceiverMemberId(matching),
                            NotificationType.MATCHING,
                            SubNotificationType.MATCHING_ACCEPTED,
                            notificationDetails));
        }
    }

    /** 발신자에게 매칭 성사 알림 발송 */
    public void alertNewAcceptedNotificationToSender(
            final Matching matching, final NotificationDetails notificationDetails) {
        if (matching.getReceiverType().equals(ReceiverType.ANNOUNCEMENT)) {
            notificationService.alertNewNotification(
                    notificationMapper.toNotification(
                            matchingInfoResolver.getSenderMemberId(matching),
                            NotificationType.ANNOUNCEMENT,
                            SubNotificationType.ANNOUNCEMENT_ACCEPTED,
                            notificationDetails));
        } else {
            notificationService.alertNewNotification(
                    notificationMapper.toNotification(
                            matchingInfoResolver.getSenderMemberId(matching),
                            NotificationType.MATCHING,
                            SubNotificationType.MATCHING_ACCEPTED,
                            notificationDetails));
        }
    }

    /** 발신자에게 매칭 거절 알림 발송 */
    public void alertNewRejectedNotificationToSender(
            final Matching matching, final NotificationDetails notificationDetails) {
        if (matching.getReceiverType().equals(ReceiverType.ANNOUNCEMENT)) {
            notificationService.alertNewNotification(
                    notificationMapper.toNotification(
                            matchingInfoResolver.getSenderMemberId(matching),
                            NotificationType.ANNOUNCEMENT,
                            SubNotificationType.ANNOUNCEMENT_REJECTED,
                            notificationDetails));
        } else {
            notificationService.alertNewNotification(
                    notificationMapper.toNotification(
                            matchingInfoResolver.getSenderMemberId(matching),
                            NotificationType.MATCHING,
                            SubNotificationType.MATCHING_REJECTED,
                            notificationDetails));
        }
    }
}
