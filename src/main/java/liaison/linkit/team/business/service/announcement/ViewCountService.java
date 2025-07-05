package liaison.linkit.team.business.service.announcement;

import java.util.Optional;
import java.util.concurrent.TimeUnit;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class ViewCountService {
    // 로그인 사용자 재요청 간격 제한 (6초)
    private static final long USER_VIEW_INTERVAL_MS = 6000;

    // SSR 중복 요청 필터링을 위한 매우 짧은 쿨다운 (500ms)
    private static final long GLOBAL_COOLDOWN_MS = 500;

    // 사용자별 조회 이력 캐시 (멤버 ID + 엔티티 기준)
    private final Cache<String, Long> userViewCache =
            Caffeine.newBuilder().expireAfterWrite(5, TimeUnit.MINUTES).maximumSize(10000).build();

    // 엔티티별 마지막 조회 시간 캐시 (엔티티 ID만 기준)
    private final Cache<String, Long> entityCooldownCache =
            Caffeine.newBuilder().expireAfterWrite(10, TimeUnit.SECONDS).maximumSize(5000).build();

    @Transactional
    public boolean processView(String entityType, Long entityId, Optional<Long> optionalMemberId) {
        // 1. 엔티티 쿨다운 확인 (로그인/비로그인 모두 적용)
        String entityKey = entityType + ":" + entityId;
        long currentTime = System.currentTimeMillis();

        // 엔티티 쿨다운 체크 (모든 사용자에게 적용)
        Long lastEntityViewTime = entityCooldownCache.getIfPresent(entityKey);
        if (lastEntityViewTime != null && (currentTime - lastEntityViewTime < GLOBAL_COOLDOWN_MS)) {
            log.debug(
                    "Entity cooldown active for {} ({}ms since last view)",
                    entityKey,
                    currentTime - lastEntityViewTime);
            return false; // 엔티티 쿨다운 기간 내 요청은 무시
        }

        // 엔티티 쿨다운 캐시 업데이트
        entityCooldownCache.put(entityKey, currentTime);

        // 2. 로그인한 사용자는 개인별 쿨다운도 추가 확인
        if (optionalMemberId.isPresent()) {
            String userKey = "m" + optionalMemberId.get() + ":" + entityKey;
            Long lastUserViewTime = userViewCache.getIfPresent(userKey);

            if (lastUserViewTime != null
                    && (currentTime - lastUserViewTime < USER_VIEW_INTERVAL_MS)) {
                log.debug(
                        "User cooldown active for {} ({}ms since last view)",
                        userKey,
                        currentTime - lastUserViewTime);
                return false; // 사용자 쿨다운 기간 내 요청은 무시
            }

            // 사용자 캐시 업데이트
            userViewCache.put(userKey, currentTime);
        }

        log.debug("New view recorded for entity: {}", entityKey);
        return true; // 조회수 증가 허용
    }
}
