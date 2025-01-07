package liaison.linkit.global.util;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionConnectedEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

@RequiredArgsConstructor
@Component
@Slf4j
public class StompHandler implements ChannelInterceptor {

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(message);

        log.info("StompHandler - Command: {}", headerAccessor.getCommand()); // 커맨드 타입 로깅
        log.info("StompHandler - Destination: {}", headerAccessor.getDestination()); // 목적지 로깅

        if (StompCommand.CONNECT.equals(headerAccessor.getCommand())) {
            log.info("STOMP Connect");
        } else if (StompCommand.SEND.equals(headerAccessor.getCommand())) {
            log.info("STOMP Send - Payload: {}", new String((byte[]) message.getPayload())); // 페이로드 로깅
        }

        return message;
    }

    @EventListener
    public void handleWebSocketConnectionListener(SessionConnectedEvent event) {
        log.info("사용자 입장: {}", event.getMessage());
    }

    @EventListener
    public void handleWebSocketDisconnectionListener(SessionDisconnectEvent event) {
        log.info("사용자 퇴장: {}", event.getMessage());
    }
}
