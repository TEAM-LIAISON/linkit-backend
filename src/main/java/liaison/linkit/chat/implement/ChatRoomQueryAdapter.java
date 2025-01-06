package liaison.linkit.chat.implement;

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
        return chatRoomRepository.findById(chatRoomId).orElseThrow(() -> ChatRoomNotFoundException.EXCEPTION);
    }

}
