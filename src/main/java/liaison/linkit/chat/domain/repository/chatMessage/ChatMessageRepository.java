package liaison.linkit.chat.domain.repository.chatMessage;

import java.util.List;
import liaison.linkit.chat.domain.ChatMessage;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

public interface ChatMessageRepository extends MongoRepository<ChatMessage, String> {

    @Query(value = "{ 'chat_room_id': ?0 }", sort = "{ 'timestamp': -1 }")
    Page<ChatMessage> findByChatRoomIdOrderByTimestampDesc(Long chatRoomId, Pageable pageable);

    // 특정 채팅방의 읽지 않은 메시지 조회
    @Query("{'chatRoomId': ?0, 'is_read': false}")
    List<ChatMessage> findByChatRoomIdAndIsReadFalseAndReceiverParticipantId(Long chatRoomId);

    @Query(
            value =
                    "{ 'chat_room_id': { $in: ?1 }, 'message_receiver_member_id': ?0, 'is_read': false }",
            count = true)
    long countUnreadMessagesByChatRoomIdsAndReceiver(Long memberId, List<Long> chatRoomIds);

    @Query(
            value = "{ 'chat_room_id': ?0, 'message_receiver_member_id': ?1, 'is_read': false }",
            count = true)
    long countUnreadMessagesInRoomForMember(Long chatRoomId, Long memberId);

    // 수정된 쿼리
    @Query("{'is_read': false, 'timestamp': {$lte: ?0}}")
    List<ChatMessage> findUnreadMessagesOlderThan(String timeThresholdStr);
}
