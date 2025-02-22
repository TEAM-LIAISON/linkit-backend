package liaison.linkit.chat.implement;

import java.util.List;

import liaison.linkit.chat.domain.ChatRoom;
import liaison.linkit.chat.domain.repository.chatRoom.ChatRoomRepository;
import liaison.linkit.chat.exception.ChatRoomNotFoundException;
import liaison.linkit.common.annotation.Adapter;
import lombok.RequiredArgsConstructor;

@Adapter
@RequiredArgsConstructor
public class ChatRoomQueryAdapter {
    private final ChatRoomRepository chatRoomRepository;

    public ChatRoom findById(final Long chatRoomId) {
        return chatRoomRepository
                .findById(chatRoomId)
                .orElseThrow(() -> ChatRoomNotFoundException.EXCEPTION);
    }

    public List<ChatRoom> findAllChatRoomsByMemberId(final Long memberId) {
        return chatRoomRepository.findAllChatRoomsByMemberId(memberId);
    }

    public boolean existsChatRoomByMatchingId(final Long matchingId) {
        return chatRoomRepository.existsChatRoomByMatchingId(matchingId);
    }

    public Long getChatRoomIdByMatchingId(final Long matchingId) {
        return chatRoomRepository.getChatRoomIdByMatchingId(matchingId);
    }

    public boolean existsChatRoomByMemberId(final Long memberId) {
        return chatRoomRepository.existsChatRoomByMemberId(memberId);
    }
}
