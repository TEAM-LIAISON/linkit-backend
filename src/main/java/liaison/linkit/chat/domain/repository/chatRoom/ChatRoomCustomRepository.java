package liaison.linkit.chat.domain.repository.chatRoom;

import java.util.List;
import liaison.linkit.chat.domain.ChatRoom;

public interface ChatRoomCustomRepository {
    List<ChatRoom> findAllChatRoomsByMemberId(final Long memberId);

    boolean existsChatRoomByMatchingId(final Long matchingId);

    Long getChatRoomIdByMatchingId(final Long matchingId);
}
