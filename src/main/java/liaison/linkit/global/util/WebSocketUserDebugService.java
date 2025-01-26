package liaison.linkit.global.util;

import java.util.Collection;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.user.SimpSession;
import org.springframework.messaging.simp.user.SimpSubscription;
import org.springframework.messaging.simp.user.SimpUser;
import org.springframework.messaging.simp.user.SimpUserRegistry;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class WebSocketUserDebugService {
    private final SimpUserRegistry simpUserRegistry;

    @Scheduled(fixedDelay = 30000)
    public void debugUserSessions() {
        // 현재 등록된 모든 사용자 조회
        Collection<SimpUser> users = simpUserRegistry.getUsers();
        for (SimpUser user : users) {
            // user.getName() => convertAndSendToUser(...) 에서 사용되는 'user' 파라미터와 동일
            log.info("현재 접속 사용자: {}", user.getName());
            // 각 사용자(세션)별 구독 경로 등도 확인 가능
            for (SimpSession session : user.getSessions()) {
                log.info(" - 세션 ID: {}", session.getId());
                for (SimpSubscription subscription : session.getSubscriptions()) {
                    log.info("   - 구독 경로: {}", subscription.getDestination());
                }
            }
        }
    }
}
