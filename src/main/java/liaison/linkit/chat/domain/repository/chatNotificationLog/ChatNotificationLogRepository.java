package liaison.linkit.chat.domain.repository.chatNotificationLog;

import java.util.List;

import liaison.linkit.chat.domain.ChatNotificationLog;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface ChatNotificationLogRepository
        extends MongoRepository<ChatNotificationLog, String> {

    // 특정 메시지 ID에 대한 알림 로그 조회
    List<ChatNotificationLog> findByChatMessageId(String chatMessageId);
}
