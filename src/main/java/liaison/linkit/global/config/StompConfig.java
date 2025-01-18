package liaison.linkit.global.config;

import liaison.linkit.global.util.StompHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketTransportRegistration;

@Configuration
@EnableWebSocketMessageBroker
@RequiredArgsConstructor
public class StompConfig implements WebSocketMessageBrokerConfigurer {

    private final StompHandler stompHandler; // WebSocket 연결 시 클라이언트의 인증 및 권한을 처리하는 인터셉터

    /**
     * 메시지 브로커 설정 - 서버와 클라이언트 간 메시지 송수신 경로를 정의 - "/app"으로 시작하는 경로는 애플리케이션으로 전달 - "/topic"은 브로드캐스트용 경로 (예: 채팅방 메시지)
     */
    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {
        config.enableSimpleBroker(
                "/sub/chat",                         // 채팅방 구독
                "/sub/header"                                          // 상단바 신규 채팅 구독
        );
        config.setApplicationDestinationPrefixes("/pub");  // /pub 경로로 서버 수신
    }

    /**
     * STOMP 엔드포인트 설정 - 클라이언트가 WebSocket 서버에 연결하기 위한 엔드포인트 - SockJS를 활성화하여 WebSocket을 지원하지 않는 브라우저에서도 사용 가능 - CORS 허용을 위해 `setAllowedOrigins("*")` 및 `setAllowedOriginPatterns("*")` 사용
     */
    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/stomp/linkit") // WebSocket 엔드포인트 정의
                .setAllowedOrigins("*"); // 모든 Origin에서의 요청을 허용 (CORS 설정)
    }

    /**
     * 클라이언트에서 서버로 들어오는 메시지 채널 설정 - StompHandler를 인터셉터로 등록하여 메시지 인증 및 추가 처리를 수행 - 예: 인증 토큰 확인, 사용자 권한 확인
     */
    @Override
    public void configureClientInboundChannel(ChannelRegistration registration) {
        registration.interceptors(stompHandler); // StompHandler를 인터셉터로 추가
        registration.taskExecutor()
                .corePoolSize(4)
                .maxPoolSize(10)
                .queueCapacity(50);
    }

    @Override
    public void configureWebSocketTransport(WebSocketTransportRegistration registration) {
        registration.setMessageSizeLimit(64 * 1024)     // 64KB
                .setSendTimeLimit(20 * 1000)        // 20 seconds
                .setSendBufferSizeLimit(512 * 1024) // 512KB
                .setTimeToFirstMessage(60 * 1000);  // 60 seconds
    }
}
