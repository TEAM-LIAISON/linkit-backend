package liaison.linkit.global.util;

import java.util.List;
import liaison.linkit.chat.event.ChatEvent.UserConnectedEvent;
import liaison.linkit.chat.event.ChatEvent.UserDisconnectedEvent;
import liaison.linkit.global.presentation.dto.ChatRoomConnectedEvent;
import liaison.linkit.global.presentation.dto.SubscribeEvent;
import liaison.linkit.login.infrastructure.JwtProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionConnectedEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

@RequiredArgsConstructor
@Component
@Slf4j
public class StompHandler implements ChannelInterceptor {

    private final JwtProvider jwtProvider;
    private final SessionRegistry sessionRegistry;
    private final ApplicationEventPublisher eventPublisher;

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(message);

        log.info("StompHandler - Command: {}", headerAccessor.getCommand());
        log.info("StompHandler - Destination: {}", headerAccessor.getDestination());

        if (StompCommand.CONNECT.equals(headerAccessor.getCommand())) {
            log.info("STOMP CONNECT 요청 수신");

            String accessToken = validateAndExtractToken(headerAccessor);
            try {
                // 토큰 검증
                jwtProvider.validateAccessToken(accessToken);

                // 사용자 ID 추출
                Long memberId = Long.valueOf(jwtProvider.getSubject(accessToken));
                log.info("인증된 사용자 ID: {}", memberId);

                String sessionId = headerAccessor.getSessionId();
                log.info("sessionId: {}", sessionId);

                // 세션 등록
                sessionRegistry.registerSession(sessionId, memberId);
                log.info("세션 등록: sessionId={}, memberId={}", sessionId, memberId);
            } catch (Exception e) {
                throw new IllegalArgumentException("유효하지 않은 토큰입니다.");
            }
        }

        if (StompCommand.SUBSCRIBE.equals(headerAccessor.getCommand())) {
            String destination = headerAccessor.getDestination();
            String sessionId = headerAccessor.getSessionId();
            Long memberId = sessionRegistry.getMemberIdBySession(sessionId);

            // 구독 경로에 따른 초기 데이터 전송
            if (destination != null) {
                if (destination.startsWith("/sub/notification/header/")) {
                    // 알림 구독시 초기 카운트 전송
                    String emailId = extractEmailId(destination);
                    log.info("emailId: {}", emailId);
                    // 이벤트 발행
                    eventPublisher.publishEvent(new SubscribeEvent(memberId, emailId));
                    log.info("알림 이벤트 발행");
                }

            }

            if (destination != null) {
                if (destination.startsWith("/sub/chat/")) {
                    // 채팅방 구독시 해당 채팅방 초기 정보 전송
                    Long chatRoomId = extractChatRoomId(destination);
                    log.info("chatRoomId: {}", chatRoomId);
                    eventPublisher.publishEvent(new ChatRoomConnectedEvent(memberId, chatRoomId));
                }
            }
            log.info("SUBSCRIBE: User [{}] subscribed to [{}] with session ID [{}]",
                    memberId, destination, sessionId);
        }

        if (StompCommand.SEND.equals(headerAccessor.getCommand())) {
            log.info("STOMP SEND 요청 수신");
            String accessToken = validateAndExtractToken(headerAccessor);

            try {
                jwtProvider.validateAccessToken(accessToken);
            } catch (Exception e) {
                throw new IllegalArgumentException("유효하지 않은 토큰입니다.");
            }

            String destination = headerAccessor.getDestination();

            // 동적으로 chatRoomId를 추출
            if (destination != null && destination.startsWith("/pub/chat/send/")) {

                // "/pub/chat/send/{chatRoomId}"에서 chatRoomId 추출
                String[] parts = destination.split("/");
                if (parts.length >= 5) { // /pub/chat/send/{chatRoomId} 구조 확인
                    String chatRoomId = parts[4];
                    log.info("Extracted chatRoomId: {}", chatRoomId);

                    String memberId = jwtProvider.getSubject(accessToken);
                    log.info("memberId: {}", memberId);

                    headerAccessor.setNativeHeader("memberId", memberId);
                    headerAccessor.setNativeHeader("chatRoomId", chatRoomId); // chatRoomId 추가

                    return MessageBuilder
                            .createMessage(message.getPayload(), headerAccessor.getMessageHeaders());
                } else {
                    throw new IllegalArgumentException("잘못된 destination 형식입니다.");
                }
            }

            // TODO
            // 채팅방 입장 입력
        }

        return message;
    }

    private String validateAndExtractToken(StompHeaderAccessor headerAccessor) {
        List<String> authorization = headerAccessor.getNativeHeader("Authorization");
        if (authorization == null || authorization.isEmpty()) {
            log.warn("Authorization 헤더가 없습니다. 연결을 거부합니다.");
            throw new IllegalArgumentException("Authorization 헤더가 필요합니다.");
        }

        String bearerToken = authorization.get(0);
        if (!bearerToken.startsWith("Bearer ")) {
            log.warn("Authorization 헤더 형식이 올바르지 않습니다.");
            throw new IllegalArgumentException("Bearer 토큰 형식이어야 합니다.");
        }

        return bearerToken.substring(7);
    }

    @EventListener
    public void handleWebSocketConnectionListener(SessionConnectedEvent event) {
        StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(event.getMessage());
        String sessionId = headerAccessor.getSessionId();
        Long memberId = sessionRegistry.getMemberIdBySession(sessionId);

        if (memberId != null) {
            eventPublisher.publishEvent(new UserConnectedEvent(memberId, sessionId));
        }

        log.info("세션 시작 처리: sessionId={}, memberId={}", sessionId, memberId);
    }

    @EventListener
    public void handleWebSocketDisconnectionListener(SessionDisconnectEvent event) {
        StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(event.getMessage());
        String sessionId = headerAccessor.getSessionId();

        // 세션 제거 및 오프라인 처리
        Long memberId = sessionRegistry.removeSession(sessionId);
        if (memberId != null) {
            eventPublisher.publishEvent(new UserDisconnectedEvent(memberId, sessionId));
            log.info("세션 종료 처리: sessionId={}, memberId={}", sessionId, memberId);
        }
    }

    private Long extractChatRoomId(String destination) {
        String[] parts = destination.split("/");
        return Long.valueOf(parts[parts.length - 1]);
    }

    private String extractEmailId(String destination) {
        String[] parts = destination.split("/");
        return parts[parts.length - 1];
    }

//    private void sendInitialChatMessages(Long memberId, Long chatRoomId) {
//        // 채팅방의 최근 메시지들을 조회하여 전송
//        messagingTemplate.convertAndSendToUser(
//            memberId.toString(),
//            "/sub/chat/" + chatRoomId,
//            chatService.getChatMessages(chatRoomId, memberId, /* pageable 객체 필요 */)
//        );
//    }
}
