package liaison.linkit.notification.service;

import liaison.linkit.chat.implement.ChatMessageQueryAdapter;
import liaison.linkit.chat.implement.ChatRoomQueryAdapter;
import liaison.linkit.member.implement.MemberQueryAdapter;
import liaison.linkit.notification.business.NotificationMapper;
import liaison.linkit.notification.implement.NotificationQueryAdapter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
    private final ChatMessageQueryAdapter chatMessageQueryAdapter;

    private final NotificationMapper notificationMapper;
    private final MemberQueryAdapter memberQueryAdapter;
    private final ChatRoomQueryAdapter chatRoomQueryAdapter;

    //    public void publishNotificationCount(final Long memberId) {
    //        final String emailId = memberQueryAdapter.findEmailIdById(memberId);
    //
    //        NotificationCountResponse count = getUnreadCount(memberId);
    //
    //        messagingTemplate.convertAndSend("/sub/notification/header/" + emailId, count);
    //    }

    //    @EventListener
    //    public void handleSubscribeEvent(final SubscribeEvent event) {
    //        Long memberId = event.getMemberId();
    //        String emailId = event.getEmailId();
    //
    //        // 초기 데이터 전송
    //        NotificationCountResponse count = getUnreadCount(memberId);
    //        messagingTemplate.convertAndSend("/sub/notification/header/" + emailId, count);
    //    }
}
