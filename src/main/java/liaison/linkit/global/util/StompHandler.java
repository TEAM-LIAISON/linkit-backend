package liaison.linkit.global.util;

import java.util.List;
import liaison.linkit.chat.event.ChatEvent.UserConnectedEvent;
import liaison.linkit.chat.event.ChatEvent.UserDisconnectedEvent;
import liaison.linkit.common.exception.RefreshTokenExpiredException;
import liaison.linkit.login.domain.MemberTokens;
import liaison.linkit.login.domain.repository.RefreshTokenRepository;
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
    private final RefreshTokenRepository refreshTokenRepository;
    private final SessionRegistry sessionRegistry;
    private final ApplicationEventPublisher eventPublisher;
    private static final String REFRESH_TOKEN = "refreshToken";

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(message);

        log.info("StompHandler - Command: {}", headerAccessor.getCommand()); // 커맨드 타입 로깅
        log.info("StompHandler - Destination: {}", headerAccessor.getDestination()); // 목적지 로깅

        if (StompCommand.CONNECT.equals(headerAccessor.getCommand())) {
            log.info("STOMP CONNECT 요청 수신");

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

            String accessToken = bearerToken.substring(7);
            String refreshToken = extractRefreshToken(headerAccessor);

            try {
                // 토큰 검증
                jwtProvider.validateTokens(new MemberTokens(accessToken, refreshToken));

                // 사용자 ID 추출
                Long memberId = Long.valueOf(jwtProvider.getSubject(accessToken));
                log.info("인증된 사용자 ID: {}", memberId);

                String sessionId = headerAccessor.getSessionId();
                log.info("sessionId: {}", sessionId);

                // 세션 등록
                sessionRegistry.registerSession(sessionId, memberId);
                log.info("세션 등록: sessionId={}, memberId={}", sessionId, memberId);
            } catch (RefreshTokenExpiredException e) {
                throw e;
            } catch (Exception e) {
                throw new IllegalArgumentException("유효하지 않은 토큰입니다.");
            }
        }

        if (StompCommand.SEND.equals(headerAccessor.getCommand())) {
            log.info("STOMP SEND 요청 수신");
            List<String> authorization = headerAccessor.getNativeHeader("Authorization");
            if (authorization == null || authorization.isEmpty()) {
                throw new IllegalArgumentException("Authorization 헤더가 필요합니다.");
            }

            String bearerToken = authorization.get(0);
            if (!bearerToken.startsWith("Bearer ")) {
                throw new IllegalArgumentException("Bearer 토큰 형식이어야 합니다.");
            }

            String accessToken = bearerToken.substring(7);
            String refreshToken = extractRefreshToken(headerAccessor);

            try {
                jwtProvider.validateTokens(new MemberTokens(accessToken, refreshToken));
            } catch (RefreshTokenExpiredException e) {
                throw e;
            } catch (Exception e) {
                throw new IllegalArgumentException("유효하지 않은 토큰입니다.");
            }

            String destination = headerAccessor.getDestination();
            if ("/pub/chat/send".equals(destination)) {
                String memberId = jwtProvider.getSubject(accessToken);

                headerAccessor.setNativeHeader("memberId", memberId);

                // 수정된 헤더로 새 메시지 생성
                return MessageBuilder
                        .createMessage(message.getPayload(), headerAccessor.getMessageHeaders());

            }
        }

        return message;
    }

    private String extractRefreshToken(StompHeaderAccessor headerAccessor) {
        List<String> cookies = headerAccessor.getNativeHeader("Cookie");
        if (cookies == null || cookies.isEmpty()) {
            log.warn("Refresh Token이 포함된 쿠키가 없습니다.");
            throw RefreshTokenExpiredException.EXCEPTION;
        }

        for (String cookieHeader : cookies) {
            String[] cookiesArray = cookieHeader.split(";");
            for (String cookie : cookiesArray) {
                String[] keyValue = cookie.trim().split("=");
                if (keyValue.length == 2 && REFRESH_TOKEN.equals(keyValue[0])) {
                    return keyValue[1];
                }
            }
        }

        log.warn("Refresh Token 쿠키를 찾을 수 없습니다.");
        throw RefreshTokenExpiredException.EXCEPTION;
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
}
