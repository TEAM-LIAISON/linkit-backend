package liaison.linkit.chat.domain.repository.chatMessage;

import java.util.List;
import liaison.linkit.chat.domain.ChatMessage;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

public interface ChatMessageRepository extends MongoRepository<ChatMessage, String> {

    Page<ChatMessage> findByChatRoomIdOrderByTimestampDesc(Long chatRoomId, Pageable pageable);

    // 특정 채팅방의 읽지 않은 메시지 조회
    @Query("{'chatRoomId': ?0, 'is_read': false}")
    List<ChatMessage> findByChatRoomIdAndIsReadFalseAndReceiverParticipantId(
            Long chatRoomId
    );

    // [추가] 특정 회원의 '읽지 않은' 메시지 수 카운트 (Derived Query)
    int countByMessageReceiverMemberIdAndIsReadFalse(Long memberId);
}
