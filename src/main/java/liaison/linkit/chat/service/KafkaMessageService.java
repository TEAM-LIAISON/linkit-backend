package liaison.linkit.chat.service;

import liaison.linkit.chat.domain.type.MessageType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class KafkaMessageService {

    private final KafkaTemplate<String, String> kafkaTemplate;

    /**
     * Kafka 메시지를 전송합니다.
     *
     * @param messageType   메시지 타입 (ENTER, TALK 등)
     * @param roomId        채팅방 ID
     * @param detailMessage 상세 메시지 내용
     * @param senderId      보낸 사람 ID
     */
    public void sendMessage(MessageType messageType, Long roomId, String detailMessage, Long senderId) {
        try {
            String topic = "chat-room-" + roomId;
            String messagePayload = String.format("{\"type\":\"%s\", \"roomId\":%d, \"detailMessage\":\"%s\", \"senderId\":%d}",
                    messageType, roomId, detailMessage, senderId);

            kafkaTemplate.send(topic, messagePayload);

            log.info("MessageKafka sent to Kafka topic: {} with payload: {}", topic, messagePayload);
        } catch (Exception e) {
            log.error("Failed to send message to Kafka", e);
            throw new RuntimeException("Kafka message sending failed", e);
        }
    }
}
