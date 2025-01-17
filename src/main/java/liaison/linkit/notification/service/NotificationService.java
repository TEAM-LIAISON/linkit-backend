package liaison.linkit.notification.service;

import java.time.LocalDateTime;
import java.util.List;
import liaison.linkit.member.implement.MemberQueryAdapter;
import liaison.linkit.notification.business.NotificationMapper;
import liaison.linkit.notification.domain.Notification;
import liaison.linkit.notification.domain.type.NotificationStatus;
import liaison.linkit.notification.domain.type.NotificationType;
import liaison.linkit.notification.implement.NotificationCommandAdapter;
import liaison.linkit.notification.implement.NotificationQueryAdapter;
import liaison.linkit.notification.presentation.dto.NotificationResponseDTO;
import liaison.linkit.notification.presentation.dto.NotificationResponseDTO.NotificationDetails;
import liaison.linkit.team.domain.team.Team;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class NotificationService {

    private final SimpMessagingTemplate messagingTemplate;

    private final NotificationQueryAdapter notificationQueryAdapter;
    private final NotificationCommandAdapter notificationCommandAdapter;
    private final NotificationMapper notificationMapper;
    private final MemberQueryAdapter memberQueryAdapter;

    public NotificationResponseDTO.NotificationItems getNotificationItems(final Long memberId) {
        final List<Notification> notifications = notificationQueryAdapter.getNotificationsByMember(memberId);
        return notificationMapper.toNotificationItems(notifications);
    }

    public void sendNotification(final Long receiverMemberId, final NotificationType notificationType, final NotificationDetails notificationDetails) {
        final String receiverEmailId = memberQueryAdapter.findEmailIdById(receiverMemberId);

        Notification notification = Notification.builder()
                .receiverMemberId(receiverMemberId)
                .notificationType(notificationType)
                .notificationStatus(NotificationStatus.PENDING)
                .createdAt(LocalDateTime.now())
                .modifiedAt(LocalDateTime.now())
                .build();

        // 알림 타입에 따라 세부 정보 설정
        switch (notificationType) {
            case MATCHING -> notification.setMatchingDetails(
                    Notification.MatchingDetails.builder()
                            .matchingSenderName(notificationDetails.getMatchingSenderName())
                            .matchingReceiverName(notificationDetails.getMatchingReceiverName())
                            .build()
            );
            case CHATTING -> notification.setChatDetails(
                    Notification.ChatDetails.builder()
                            .senderName(notificationDetails.getSenderName())
                            .receiverName(notificationDetails.getReceiverName())
                            .build()
            );
            case TEAM_INVITATION -> notification.setInvitationDetails(
                    Notification.InvitationDetails.builder()
                            .teamName(notificationDetails.getTeamName())
                            .build()
            );
            case SYSTEM -> notification.setSystemDetails(
                    Notification.SystemDetails.builder()
                            .systemMessage(notificationDetails.getSystemMessage())
                            .build()
            );
        }

        // DB에 알림 저장
        Notification savedNotification = notificationCommandAdapter.save(notification);

        // WebSocket을 통해 실시간 알림 전송
        NotificationResponseDTO.NotificationItem notificationItem = notificationMapper.toNotificationItem(savedNotification);

        if (receiverEmailId != null) {
            // 수신자별 구독 주소로 알림 전송
            messagingTemplate.convertAndSendToUser(
                    receiverMemberId.toString(),
                    String.format("/sub/notification/%s", receiverEmailId), // emailId를 동적으로 경로에 삽입
                    notificationItem
            );
            log.info("Sent notification to " + receiverEmailId);
        } else {
            log.warn("수신자의 emailId가 존재하지 않습니다. 알림 전송이 중단되었습니다.");
        }
    }

    public void addInvitationNotificationsForTeams(final Long memberId, final List<Team> invitationTeams) {
        for (Team team : invitationTeams) {
            // 각 팀별로 초대 알림 생성
            NotificationDetails notificationDetails = NotificationDetails.invitation(
                    team.getTeamName()
            );

            // 알림 전송
            sendNotification(
                    memberId,
                    NotificationType.TEAM_INVITATION,
                    notificationDetails
            );
        }
    }
}
