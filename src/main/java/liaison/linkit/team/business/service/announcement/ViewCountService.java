package liaison.linkit.team.business.service.announcement;

import java.util.Optional;
import java.util.UUID;
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
    // 재요청 간격 제한 (6초)
    private static final long MIN_VIEW_INTERVAL_MS = 6000;

    // 로컬 캐시에 타임스탬프 저장 (5분 후 만료)
    private final Cache<String, Long> localCache =
            Caffeine.newBuilder().expireAfterWrite(5, TimeUnit.MINUTES).maximumSize(10000).build();

    @Transactional
    public boolean processView(String entityType, Long entityId, Optional<Long> optionalMemberId) {
        // 식별자 생성 - HttpServletRequest 사용하지 않음
        String identifier = generateIdentifier(optionalMemberId);
        String cacheKey = entityType + ":" + entityId + ":" + identifier;

        log.debug("Processing view with key: {}", cacheKey);

        // 현재 시간 가져오기
        long currentTime = System.currentTimeMillis();

        // 로컬 캐시에서 마지막 조회 시간 확인
        Long lastViewTime = localCache.getIfPresent(cacheKey);

        // 3초 이내 재요청 체크
        if (lastViewTime != null && (currentTime - lastViewTime < MIN_VIEW_INTERVAL_MS)) {
            // 로컬 캐시 시간 업데이트
            localCache.put(cacheKey, currentTime);
            log.debug("Ignored view within 3 seconds: {}", cacheKey);
            return false; // 3초 내 재요청으로 조회수 증가하지 않음
        }

        // 로컬 캐시에 현재 시간 저장
        localCache.put(cacheKey, currentTime);
        log.debug("New view recorded: {}", cacheKey);

        return true;
    }

    private String generateIdentifier(Optional<Long> optionalMemberId) {
        if (optionalMemberId.isPresent()) {
            // 로그인 사용자는 멤버 ID 기반 식별
            return "m" + optionalMemberId.get();
        } else {
            // 비로그인 사용자는 랜덤 ID 생성
            // 각 이벤트마다 새로운 ID를 생성하므로
            // 동일 비로그인 사용자의 짧은 시간 내 중복 조회는 무시됨
            return "g" + UUID.randomUUID().toString().substring(0, 10);
        }
    }
}
