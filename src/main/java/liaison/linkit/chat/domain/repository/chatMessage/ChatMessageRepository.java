//package liaison.linkit.chat.domain.repository.chatMessage;
//
//import liaison.linkit.chat.domain.ChatMessage;
//import org.springframework.data.mongodb.repository.MongoRepository;
//import org.springframework.data.mongodb.repository.Query;
//
//import java.util.List;
//
//public interface ChatMessageRepository extends MongoRepository<ChatMessage, String> {
//
//    List<ChatMessage> findByChatRoomIdOrderByTimestampDesc(final Long chatRoomId);
//
//    ChatMessage findFirstByChatRoomIdOrderByTimestampDesc(String chatRoomId);
//
//    long countByChatRoomIdAndIsReadFalseAndSenderIdNot(
//            String chatRoomId, Long excludeUserId);
//
//    @Query("{'chatRoomId': ?0, 'senderId': {$ne: ?1}, 'isRead': false}")
//    List<ChatMessage> findUnreadMessages(String chatRoomId, Long userId);
//}
