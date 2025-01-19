package liaison.linkit.notification.service;

import liaison.linkit.chat.implement.ChatQueryAdapter;
import liaison.linkit.global.presentation.dto.SubscribeEvent;
import liaison.linkit.member.implement.MemberQueryAdapter;
import liaison.linkit.notification.business.NotificationMapper;
import liaison.linkit.notification.implement.NotificationQueryAdapter;
import liaison.linkit.notification.presentation.dto.NotificationResponseDTO.NotificationCountResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class HeaderNotificationService {
    private final SimpMessagingTemplate messagingTemplate;

    private final NotificationQueryAdapter notificationQueryAdapter;
    private final ChatQueryAdapter chatQueryAdapter;

    private final NotificationMapper notificationMapper;
    private final MemberQueryAdapter memberQueryAdapter;

    public void publishNotificationCount(final Long memberId) {
        final String emailId = memberQueryAdapter.findEmailIdById(memberId);

        NotificationCountResponse count = getUnreadCount(memberId);
        log.info("Sent notification to " + emailId);
        messagingTemplate.convertAndSend("/sub/notification/header/" + emailId, count);
    }

    public NotificationCountResponse getUnreadCount(final Long memberId) {
        long unreadNotificationCount = notificationQueryAdapter.countUnreadMessages(memberId);
        long unreadChatCount = chatQueryAdapter.countByMessageReceiverMemberIdAndIsReadFalse(memberId);

        return notificationMapper.toNotificationCount(unreadChatCount, unreadNotificationCount);
    }

    @EventListener
    public void handleSubscribeEvent(final SubscribeEvent event) {
        Long memberId = event.getMemberId();
        String emailId = event.getEmailId();

        // 초기 데이터 전송
        NotificationCountResponse count = getUnreadCount(memberId);
        messagingTemplate.convertAndSend("/sub/notification/header/" + emailId, count);
    }
}
