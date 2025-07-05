package liaison.linkit.notification.service;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import liaison.linkit.chat.business.ChatMapper;
import liaison.linkit.chat.domain.ChatMessage;
import liaison.linkit.chat.domain.ChatRoom;
import liaison.linkit.chat.domain.repository.chatMessage.ChatMessageRepository;
import liaison.linkit.chat.implement.ChatMessageQueryAdapter;
import liaison.linkit.chat.implement.ChatRoomQueryAdapter;
import liaison.linkit.chat.presentation.dto.ChatResponseDTO;
import liaison.linkit.global.presentation.dto.ChatRoomConnectedEvent;
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
    private final ChatMessageQueryAdapter chatMessageQueryAdapter;
    private final ChatMessageRepository chatMessageRepository;
    private final ChatMapper chatMapper;

    @EventListener
    public void handleChatRoomConnectedEvent(final ChatRoomConnectedEvent event) {
        Long memberId = event.getMemberId();
        Long chatRoomId = event.getChatRoomId();

        Executors.newSingleThreadScheduledExecutor()
                .schedule(
                        () -> {
                            //                            ChatResponseDTO.ChatMessageResponse
                            // response =
                            //                                    getChatRoomState(memberId,
                            // chatRoomId);
                            //                            // (1) 유저별 전용 경로로 전송
                            //                            messagingTemplate.convertAndSendToUser(
                            //                                    memberId.toString(), "/sub/chat/"
                            // + chatRoomId, response);

                        },
                        300,
                        TimeUnit.MILLISECONDS);
    }

    /**
     * 채팅방에 처음 접속했을 때, 채팅방의 상태를 조회합니다. 상대방이 이미 채팅방에 접속한 상태에서 내가 들어오는 경우와 아무도 접속하지 않은 상태에서 내가 처음 들어오는
     * 경우를 처리합니다.
     *
     * @param memberId 채팅방에 접속한 사람
     * @param chatRoomId 채팅방 ID
     * @return Optional<ChatResponseDTO.ChatMessageResponse> - 상대방이 채팅방에 있을 때만 응답 반환
     */
    private Optional<ChatResponseDTO.ChatMessageResponse> getInitSubscribeChatMessageResponse(
            final Long memberId, final Long chatRoomId) {
        // 1. 채팅방 조회
        ChatRoom chatRoom = chatRoomQueryAdapter.findById(chatRoomId);
        if (chatRoom == null) {
            return Optional.empty();
        }

        // 2. 상대방 ID 찾기
        Long partnerId = getChatPartnerId(chatRoom, memberId);

        // 3. 상대방이 채팅방을 보고 있는지 확인 (채팅방 구독 여부 확인)
        boolean isChatPartnerIsJoinChatRoom =
                sessionRegistry.isSubscribedToChatRoom(chatRoomId, partnerId);

        // 4. 상대방이 채팅방에 입장하지 않은 상태라면 응답을 생성하지 않음
        if (!isChatPartnerIsJoinChatRoom) {
            return Optional.empty();
        }

        // 5. 채팅방의 마지막 메시지를 조회
        ChatMessage lastChatMessage =
                chatMessageQueryAdapter.findLatestMessageByChatRoomId(chatRoomId);

        // 6. 마지막 메시지가 내 메시지인지 확인
        boolean isLastMessageIsMyMessage =
                lastChatMessage.getMessageSenderMemberId().equals(memberId);

        // 7. 상대방 메시지인 경우 읽음 처리 (읽음 상태 업데이트)
        boolean isLastMessageRead = lastChatMessage.isRead();

        if (!isLastMessageIsMyMessage && !isLastMessageRead) {
            lastChatMessage.markAsRead();
            chatMessageRepository.save(lastChatMessage);

            // 저장 후 최신 상태 다시 조회 (읽음 상태가 DB에 반영된 후)
            lastChatMessage =
                    chatMessageQueryAdapter.findLatestMessageByChatRoomId(
                            chatRoomId); // 만약 조회 실패하면 원래 객체 유지

            // 업데이트된 읽음 상태 확인
            isLastMessageRead = lastChatMessage.isRead();
        }

        // 9. 응답 DTO 생성 및 반환
        return Optional.of(
                chatMapper.toChatMessageResponse(
                        chatRoom,
                        lastChatMessage,
                        memberId,
                        isChatPartnerIsJoinChatRoom,
                        lastChatMessage.getId(),
                        isLastMessageIsMyMessage,
                        isLastMessageRead));
    }

    public ChatRoomConnectedInitResponse getChatRoomState(
            final Long memberId, final Long chatRoomId) {
        // 1. 채팅방 조회
        ChatRoom chatRoom = chatRoomQueryAdapter.findById(chatRoomId);
        // 2. 상대방 ID 찾기
        Long partnerId = getChatPartnerId(chatRoom, memberId);
        // 3. 상대방 온라인 여부
        boolean isPartnerOnline = sessionRegistry.isOnline(partnerId);
        // 4. 읽지 않은 메시지 개수
        long unreadCount =
                chatMessageQueryAdapter.countUnreadMessagesInRoomForMember(chatRoomId, memberId);
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
