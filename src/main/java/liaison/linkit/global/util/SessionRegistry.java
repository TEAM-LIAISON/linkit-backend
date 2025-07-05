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

    /** sessionId -> memberId 매핑 */
    private final Map<String, Long> sessionToMemberMap = new ConcurrentHashMap<>();

    /** memberId -> 여러 세션(sessionId) 매핑 */
    private final Map<Long, Set<String>> memberToSessionsMap = new ConcurrentHashMap<>();

    /** chatRoomId -> 구독 중인 memberId 목록 */
    private final Map<Long, Set<Long>> chatRoomSubscribersMap = new ConcurrentHashMap<>();

    /** sessionId로 memberId 조회 */
    public Long getMemberIdBySession(String sessionId) {
        Long memberId = sessionToMemberMap.get(sessionId);
        log.debug("getMemberIdBySession: sessionId={}, memberId={}", sessionId, memberId);
        return memberId;
    }

    /** 세션 등록 (CONNECT 시점) */
    public void registerSession(String sessionId, Long memberId) {
        sessionToMemberMap.put(sessionId, memberId);

        memberToSessionsMap
                .computeIfAbsent(memberId, k -> ConcurrentHashMap.newKeySet())
                .add(sessionId);

        log.debug("registerSession: sessionId={}, memberId={}", sessionId, memberId);
    }

    /**
     * 세션 제거 (DISCONNECT 시점)
     *
     * @return memberId (만약 해당 memberId의 모든 세션이 사라졌다면 오프라인 상태)
     */
    public Long removeSession(String sessionId) {
        Long memberId = sessionToMemberMap.remove(sessionId);
        if (memberId != null) {
            Set<String> sessionIds = memberToSessionsMap.get(memberId);
            if (sessionIds != null) {
                sessionIds.remove(sessionId);
                if (sessionIds.isEmpty()) {
                    memberToSessionsMap.remove(memberId);
                    return memberId; // 이 시점에 '오프라인' 처리 필요할 수 있음
                }
            }
        }
        // null이면 아직 다른 세션이 남아 있거나 sessionId가 없었던 경우
        return null;
    }

    /** 해당 memberId가 현재 온라인(1개 이상의 세션이 존재)인지 확인 */
    public boolean isOnline(Long memberId) {
        return hasActiveSessions(memberId);
    }

    /** 해당 memberId가 가진 활성 세션이 있는지 확인 */
    public boolean hasActiveSessions(Long memberId) {
        Set<String> sessionIds = memberToSessionsMap.get(memberId);
        return (sessionIds != null && !sessionIds.isEmpty());
    }

    /** 특정 memberId가 가진 모든 sessionId 반환 */
    public Set<String> getMemberSessions(Long memberId) {
        return memberToSessionsMap.getOrDefault(memberId, Collections.emptySet());
    }

    /** 채팅방 구독 등록 (SUBSCRIBE 시점) */
    public void subscribeToChatRoom(Long chatRoomId, Long memberId) {
        chatRoomSubscribersMap
                .computeIfAbsent(chatRoomId, k -> ConcurrentHashMap.newKeySet())
                .add(memberId);
        log.debug("subscribeToChatRoom: chatRoomId={}, memberId={}", chatRoomId, memberId);
    }

    /** 채팅방 구독 해제 (UNSUBSCRIBE 시점) */
    public void unsubscribeFromChatRoom(Long chatRoomId, Long memberId) {
        Set<Long> subscribers = chatRoomSubscribersMap.get(chatRoomId);
        if (subscribers != null) {
            subscribers.remove(memberId);
            if (subscribers.isEmpty()) {
                chatRoomSubscribersMap.remove(chatRoomId);
            }
        }
        log.debug("unsubscribeFromChatRoom: chatRoomId={}, memberId={}", chatRoomId, memberId);
    }

    /** 특정 사용자가 특정 채팅방을 구독 중인지 확인 */
    public boolean isSubscribedToChatRoom(Long chatRoomId, Long memberId) {
        Set<Long> subscribers = chatRoomSubscribersMap.get(chatRoomId);
        return subscribers != null && subscribers.contains(memberId);
    }

    /** 특정 채팅방의 모든 구독자 목록 반환 */
    public Set<Long> getChatRoomSubscribers(Long chatRoomId) {
        return chatRoomSubscribersMap.getOrDefault(chatRoomId, Collections.emptySet());
    }
}
