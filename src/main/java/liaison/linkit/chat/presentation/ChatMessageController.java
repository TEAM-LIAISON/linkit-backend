package liaison.linkit.chat.presentation;

import liaison.linkit.chat.presentation.dto.ChatRequestDTO.ChatMessageRequest;
import liaison.linkit.chat.service.ChatService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.Message;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Controller;

@Controller
@RequiredArgsConstructor
@Slf4j
public class ChatMessageController {

    private final ChatService chatService;

    // ==============================
    // 2) STOMP: 메시지 전송
    // ==============================

    /**
     * 클라이언트가 /pub/chat/send 로 메시지를 전송하면 서버가 이 메서드를 통해 메시지를 수신하고, 내부 로직 처리 후 /sub/chat/{chatRoomId} 경로로 메시지를 브로드캐스트한다.
     */
    @MessageMapping("/chat/send")
    public void sendChatMessage(
            @Payload ChatMessageRequest chatMessageRequest,
            @Header(name = "memberId", required = true) Long memberId,
            @Header(name = "chatRoomId", required = true) Long chatRoomId,
            Message<?> message
    ) {
        // StompHeaderAccessor로 message의 정보를 추출
        StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(message);
        String destination = headerAccessor.getDestination();
        log.info("Destination: {}", destination);

        // /pub/chat/send/{chatRoomId}에서 chatRoomId 추출
        chatService.handleChatMessage(chatMessageRequest, memberId, chatRoomId);
    }


}
