package liaison.linkit.chat.presentation;

import java.util.concurrent.ExecutionException;
import liaison.linkit.chat.domain.type.MessageType;
import liaison.linkit.chat.presentation.dto.MessageDTO;
import liaison.linkit.chat.service.KafkaMessageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@Slf4j
public class MessageController {
    private final KafkaMessageService kafkaMessageService;
    private final SimpMessagingTemplate sendingOperations; // 메시지를 클라이언트로 전송하는 Bean

    @MessageMapping("/message")
    public void sendMessage(MessageDTO message) throws ExecutionException, InterruptedException {
        if (MessageType.ENTER.equals(message.getType())) {
            message.setDetailMessage(message.getSenderId() + "님이 입장하였습니다.");
            kafkaMessageService.sendMessage(
                    MessageType.ENTER,
                    message.getRoomId(),
                    message.getDetailMessage(),
                    message.getSenderId()
            );
        } else {
            kafkaMessageService.sendMessage(
                    MessageType.TALK,
                    message.getRoomId(),
                    message.getDetailMessage(),
                    message.getSenderId()
            );
        }

        log.info("roomID = {}", message.getRoomId());
        sendingOperations.convertAndSend("/topic/chat/room/" + message.getRoomId(), message);
    }

    @MessageMapping("/subscribe")
    public void subscribeToRoom(Long roomId) {
        log.info("Subscribed to room: {}", roomId);
    }
}
