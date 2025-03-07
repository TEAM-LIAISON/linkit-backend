package liaison.linkit.global.util;

import java.security.Principal;
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

        if (StompCommand.CONNECT.equals(headerAccessor.getCommand())) {

            String accessToken = validateAndExtractToken(headerAccessor);
            try {
                // 토큰 검증
                jwtProvider.validateAccessToken(accessToken);

                Long memberId = Long.valueOf(jwtProvider.getSubject(accessToken));
                String sessionId = headerAccessor.getSessionId();

                sessionRegistry.registerSession(sessionId, memberId);

                UserPrincipal principal = new UserPrincipal(memberId.toString());
                headerAccessor.setUser(principal); // Principal 설정
            } catch (Exception e) {
                throw new IllegalArgumentException("유효하지 않은 토큰입니다.");
            }
        }

        if (StompCommand.SUBSCRIBE.equals(headerAccessor.getCommand())) {
            String destination = headerAccessor.getDestination();
            String sessionId = headerAccessor.getSessionId();
            Principal user = headerAccessor.getUser();
            Long memberId = sessionRegistry.getMemberIdBySession(sessionId);

            // 구독 경로에 따른 초기 데이터 전송
            if (destination != null) {
                if (destination.startsWith("/sub/notification/header/")) {
                    String emailId = extractEmailId(destination);
                    eventPublisher.publishEvent(new SubscribeEvent(memberId, emailId));
                }
            }

            if (destination != null) {
                if (destination.startsWith("/user/sub/chat/")) {
                    Long chatRoomId = extractChatRoomId(destination);
                    eventPublisher.publishEvent(new ChatRoomConnectedEvent(memberId, chatRoomId));
                }
            }
        }

        if (StompCommand.SEND.equals(headerAccessor.getCommand())) {
            String accessToken = validateAndExtractToken(headerAccessor);

            try {
                jwtProvider.validateAccessToken(accessToken);
            } catch (Exception e) {
                throw new IllegalArgumentException("유효하지 않은 토큰입니다.");
            }

            String destination = headerAccessor.getDestination();
            String sessionId = headerAccessor.getSessionId();

            // 동적으로 chatRoomId를 추출
            if (destination != null && destination.startsWith("/pub/chat/send/")) {

                // "/pub/chat/send/{chatRoomId}"에서 chatRoomId 추출
                String[] parts = destination.split("/");
                if (parts.length >= 5) { // /pub/chat/send/{chatRoomId} 구조 확인
                    String chatRoomId = parts[4];

                    String memberId = jwtProvider.getSubject(accessToken);
                    log.debug("SEND Command - Extracted memberId: {}", memberId); // 로그 추가

                    headerAccessor.setNativeHeader("memberId", memberId);
                    headerAccessor.setNativeHeader("sessionId", sessionId);
                    headerAccessor.setNativeHeader("chatRoomId", chatRoomId); // chatRoomId 추가

                    return MessageBuilder.createMessage(
                            message.getPayload(), headerAccessor.getMessageHeaders());
                } else {
                    throw new IllegalArgumentException("잘못된 destination 형식입니다.");
                }
            }

            // 채팅방 입장 입력
            if (destination != null && destination.startsWith("/pub/chat/read")) {
                // "/pub/chat/send/{chatRoomId}"에서 chatRoomId 추출
                String[] parts = destination.split("/");
                if (parts.length >= 5) { // /pub/chat/read/{chatRoomId} 구조 확인
                    String chatRoomId = parts[4];
                    String memberId = jwtProvider.getSubject(accessToken);

                    headerAccessor.setNativeHeader("memberId", memberId);
                    headerAccessor.setNativeHeader("chatRoomId", chatRoomId); // chatRoomId 추가

                    return MessageBuilder.createMessage(
                            message.getPayload(), headerAccessor.getMessageHeaders());
                } else {
                    throw new IllegalArgumentException("잘못된 destination 형식입니다.");
                }
            }
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
    }

    @EventListener
    public void handleSessionConnected(SessionConnectedEvent event) {
        StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(event.getMessage());
        Principal userPrincipal = headerAccessor.getUser();
    }

    @EventListener
    public void handleWebSocketDisconnectionListener(SessionDisconnectEvent event) {
        StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(event.getMessage());
        String sessionId = headerAccessor.getSessionId();

        // 세션 제거 및 오프라인 처리
        Long memberId = sessionRegistry.removeSession(sessionId);
        if (memberId != null) {
            eventPublisher.publishEvent(new UserDisconnectedEvent(memberId, sessionId));
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

    // private void sendInitialChatMessages(Long memberId, Long chatRoomId) {
    // // 채팅방의 최근 메시지들을 조회하여 전송
    // messagingTemplate.convertAndSendToUser(
    // memberId.toString(),
    // "/sub/chat/" + chatRoomId,
    // chatService.getChatMessages(chatRoomId, memberId, /* pageable 객체 필요 */)
    // );
    // }
}
