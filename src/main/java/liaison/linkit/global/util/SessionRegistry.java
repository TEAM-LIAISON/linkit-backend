package liaison.linkit.global.util;

import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class SessionRegistry {
    private final Map<String, Long> sessions = new ConcurrentHashMap<>();
    private final Map<Long, Set<String>> memberSessions = new ConcurrentHashMap<>();

    public Long getMemberIdBySession(String sessionId) {
        Long memberId = sessions.get(sessionId);
        log.info("Getting memberId for session: sessionId={}, memberId={}", sessionId, memberId);
        return memberId;
    }

    public void registerSession(String sessionId, Long memberId) {
        sessions.put(sessionId, memberId);
        memberSessions.computeIfAbsent(memberId, k -> ConcurrentHashMap.newKeySet()).add(sessionId);
    }

    public Long removeSession(String sessionId) {
        Long memberId = sessions.remove(sessionId);
        if (memberId != null) {
            Set<String> memberSessionIds = memberSessions.get(memberId);
            if (memberSessionIds != null) {
                memberSessionIds.remove(sessionId);
                if (memberSessionIds.isEmpty()) {
                    memberSessions.remove(memberId);
                    log.info("사용자의 모든 세션 종료: memberId={}", memberId);
                    return memberId;  // 실제 오프라인 처리가 필요한 경우
                }
            }
        }
        return null;  // 아직 다른 세션이 있는 경우
    }

    public boolean hasActiveSessions(Long memberId) {
        Set<String> memberSessionIds = memberSessions.get(memberId);
        return memberSessionIds != null && !memberSessionIds.isEmpty();
    }

    public Set<String> getMemberSessions(Long memberId) {
        return memberSessions.getOrDefault(memberId, Collections.emptySet());
    }
}
