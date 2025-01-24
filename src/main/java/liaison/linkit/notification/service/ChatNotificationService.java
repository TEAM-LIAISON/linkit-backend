package liaison.linkit.notification.service;

import java.time.LocalDateTime;
import liaison.linkit.chat.domain.ChatRoom;
import liaison.linkit.chat.event.ChatEvent.ChatRoomConnectedEvent;
import liaison.linkit.chat.implement.ChatQueryAdapter;
import liaison.linkit.chat.implement.ChatRoomQueryAdapter;
import liaison.linkit.global.util.SessionRegistry;
import liaison.linkit.notification.presentation.dto.NotificationResponseDTO.ChatRoomConnectedInitResponse;
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
public class ChatNotificationService {

    private final SimpMessagingTemplate messagingTemplate;
    private final SessionRegistry sessionRegistry;
    // 채팅방 및 메시지 쿼리
    private final ChatRoomQueryAdapter chatRoomQueryAdapter;
    private final ChatQueryAdapter chatQueryAdapter;

    @EventListener
    public void handleChatRoomConnectedEvent(final ChatRoomConnectedEvent event) {
        Long memberId = event.getMemberId();
        Long chatRoomId = event.getChatRoomId();

        ChatRoomConnectedInitResponse response = getChatRoomState(memberId, chatRoomId);

        messagingTemplate.convertAndSend("/sub/chat/" + chatRoomId, response);
    }

    private ChatRoomConnectedInitResponse getChatRoomState(final Long memberId, final Long chatRoomId) {
        // 1. 채팅방 조회
        ChatRoom chatRoom = chatRoomQueryAdapter.findById(chatRoomId);

        // 2. 상대방 ID 찾기
        Long partnerId = getChatPartnerId(chatRoom, memberId);

        // 3. 상대방 온라인 여부
        boolean isPartnerOnline = sessionRegistry.isOnline(partnerId);

        // 4. 읽지 않은 메시지 개수
        long unreadCount = chatQueryAdapter.countUnreadMessagesInRoomForMember(chatRoomId, memberId);

        // 5. 마지막 메시지 정보 (ChatRoom 엔티티가 lastMessage, lastMessageTime 필드를 가지고 있다고 가정)
        //    만약 ChatRoom이 아니라 ChatMessageRepository에서 직접 최신 메시지를 조회한다면 변경 필요
        String lastMessage = chatRoom.getLastMessage();
        LocalDateTime lastMessageTime = chatRoom.getLastMessageTime();

        // 6. 응답 DTO 생성
        return ChatRoomConnectedInitResponse.builder()
                .isChatPartnerOnline(isPartnerOnline)
                .unreadChatMessageCount(unreadCount)
                .lastMessage(lastMessage)
                .lastMessageTime(lastMessageTime)
                .build();
    }

    private Long getChatPartnerId(ChatRoom chatRoom, Long memberId) {
        // 예: participantA == memberId 이면 partner = participantB, 아니면 반대
        if (chatRoom.getParticipantAMemberId().equals(memberId)) {
            return chatRoom.getParticipantBMemberId();
        } else {
            return chatRoom.getParticipantAMemberId();
        }
    }

}
