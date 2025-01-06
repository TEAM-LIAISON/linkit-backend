package liaison.linkit.chat.domain.repository.chatMessage;

import java.util.List;
import liaison.linkit.chat.domain.ChatMessage;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface ChatMessageRepository extends MongoRepository<ChatMessage, String> {
    
    Page<ChatMessage> findByChatRoomIdOrderByTimestampDesc(Long chatRoomId, Pageable pageable);

    List<ChatMessage> findByChatRoomIdAndIsReadFalseAndSenderMemberIdNot(
            Long chatRoomId,
            Long excludeMemberId
    );
}
