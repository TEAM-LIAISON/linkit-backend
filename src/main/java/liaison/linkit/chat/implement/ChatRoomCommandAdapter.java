package liaison.linkit.chat.implement;

import liaison.linkit.chat.domain.ChatRoom;
import liaison.linkit.chat.domain.repository.chatRoom.ChatRoomRepository;
import liaison.linkit.common.annotation.Adapter;
import lombok.RequiredArgsConstructor;

@Adapter
@RequiredArgsConstructor
public class ChatRoomCommandAdapter {
    private final ChatRoomRepository chatRoomRepository;

    public ChatRoom createChatRoom(final ChatRoom chatRoom) {
        return chatRoomRepository.save(chatRoom);
    }

}
