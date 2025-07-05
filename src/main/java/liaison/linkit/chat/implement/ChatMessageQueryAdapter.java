package liaison.linkit.chat.implement;

import java.util.List;

import liaison.linkit.chat.domain.ChatMessage;
import liaison.linkit.chat.domain.repository.chatMessage.ChatMessageRepository;
import liaison.linkit.common.annotation.Adapter;
import lombok.RequiredArgsConstructor;

@Adapter
@RequiredArgsConstructor
public class ChatMessageQueryAdapter {
    private final ChatMessageRepository chatMessageRepository;

    public long countUnreadMessagesByChatRoomIdsAndReceiver(
            final Long memberId, final List<Long> chatRoomIds) {
        return chatMessageRepository.countUnreadMessagesByChatRoomIdsAndReceiver(
                memberId, chatRoomIds);
    }

    public long countUnreadMessagesInRoomForMember(final Long chatRoomId, final Long memberId) {
        return chatMessageRepository.countUnreadMessagesInRoomForMember(chatRoomId, memberId);
    }

    /**
     * 특정 채팅방의 가장 최근 메시지를 조회합니다. 채팅방 목록이나 채팅방 미리보기에 사용될 수 있습니다.
     *
     * @param chatRoomId 채팅방 ID
     * @return 해당 채팅방의 가장 최근 메시지 (Optional로 래핑됨)
     */
    public ChatMessage findLatestMessageByChatRoomId(final Long chatRoomId) {
        return chatMessageRepository.findTopByChatRoomIdOrderByTimestampDesc(chatRoomId);
    }
}
