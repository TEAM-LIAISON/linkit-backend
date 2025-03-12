package liaison.linkit.chat.service;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import liaison.linkit.chat.event.ChatEvent;
import liaison.linkit.global.util.SessionRegistry;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class ChatStatusService {
    private final SimpMessagingTemplate messagingTemplate;
    private final SessionRegistry sessionRegistry;
    private final Map<Long, Set<String>> userSessions = new ConcurrentHashMap<>();

    @EventListener
    public void handleUserConnected(ChatEvent.UserConnectedEvent event) {
        Long memberId = event.getMemberId();
        String sessionId = event.getSessionId();

        userSessions.computeIfAbsent(memberId, k -> ConcurrentHashMap.newKeySet()).add(sessionId);

        broadcastUserStatus(memberId, true);
    }

    @EventListener
    public void handleUserDisconnected(ChatEvent.UserDisconnectedEvent event) {
        Long memberId = event.getMemberId();
        String sessionId = event.getSessionId();

        Set<String> sessions = userSessions.get(memberId);
        if (sessions != null) {
            sessions.remove(sessionId);
            if (sessions.isEmpty()) {
                userSessions.remove(memberId);
                broadcastUserStatus(memberId, false);
            }
        }
    }

    private void broadcastUserStatus(Long memberId, boolean isOnline) {
        messagingTemplate.convertAndSend(
                "/sub/status", Map.of("memberId", memberId, "online", isOnline));
    }

    public boolean isUserOnline(Long memberId) {
        return sessionRegistry.hasActiveSessions(memberId);
    }
}
