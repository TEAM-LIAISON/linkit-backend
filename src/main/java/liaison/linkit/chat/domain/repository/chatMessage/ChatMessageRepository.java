package liaison.linkit.chat.domain.repository.chatMessage;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

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

    // 읽지 않은 메시지 중 특정 시간보다 오래된 메시지 조회 - 필드명 확인
    @Query(value = "{ 'is_read': false, 'timestamp': {$lte: ?0} }")
    List<ChatMessage> findUnreadMessagesOlderThan(LocalDateTime timeThreshold);

    // 특정 채팅방의 마지막 메시지 조회 (timestamp 기준 내림차순 정렬 후 첫 번째 항목)
    @Query(value = "{ 'chat_room_id': ?0 }", sort = "{ 'timestamp': -1 }")
    Optional<ChatMessage> findFirstByChatRoomIdOrderByTimestampDesc(Long chatRoomId);

    /**
     * 특정 채팅방의 가장 최근 메시지를 조회합니다. 채팅방 목록이나 채팅방 미리보기에 사용됩니다.
     *
     * @param chatRoomId 채팅방 ID
     * @return 해당 채팅방의 가장 최근 메시지 (Optional로 래핑됨)
     */
    @Query(value = "{ 'chat_room_id': ?0 }", sort = "{ 'timestamp': -1 }")
    ChatMessage findTopByChatRoomIdOrderByTimestampDesc(Long chatRoomId);
}
