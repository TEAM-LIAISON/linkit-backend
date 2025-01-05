package liaison.linkit.chat.implement;

import liaison.linkit.chat.domain.repository.chatRoom.ChatRoomRepository;
import liaison.linkit.common.annotation.Adapter;
import lombok.RequiredArgsConstructor;

@Adapter
@RequiredArgsConstructor
public class ChatRoomQueryAdapter {
    private final ChatRoomRepository chatRoomRepository;
}
