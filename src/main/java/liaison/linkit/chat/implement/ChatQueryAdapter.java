package liaison.linkit.chat.implement;

import java.util.List;
import liaison.linkit.chat.domain.repository.chatMessage.ChatMessageRepository;
import liaison.linkit.common.annotation.Adapter;
import lombok.RequiredArgsConstructor;

@Adapter
@RequiredArgsConstructor
public class ChatQueryAdapter {
    private final ChatMessageRepository chatMessageRepository;

    public long countUnreadMessagesByChatRoomIdsAndReceiver(final Long memberId, final List<Long> chatRoomIds) {
        return chatMessageRepository.countUnreadMessagesByChatRoomIdsAndReceiver(memberId, chatRoomIds);
    }

    public long countUnreadMessagesInRoomForMember(final Long chatRoomId, final Long memberId) {
        return chatMessageRepository.countUnreadMessagesInRoomForMember(chatRoomId, memberId);
    }
}
