package liaison.linkit.team.business.service.announcement;

import java.util.Optional;
import java.util.concurrent.TimeUnit;

import jakarta.servlet.http.HttpServletRequest;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import lombok.RequiredArgsConstructor;
import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ViewCountService {
    private final HttpServletRequest httpServletRequest;

    // 재요청 간격 제한 (3초)
    private static final long MIN_VIEW_INTERVAL_MS = 3000;

    // 로컬 캐시에 타임스탬프 저장 (5분 후 만료)
    private final Cache<String, Long> localCache =
            Caffeine.newBuilder().expireAfterWrite(5, TimeUnit.MINUTES).maximumSize(10000).build();

    @Transactional
    public boolean processView(String entityType, Long entityId, Optional<Long> optionalMemberId) {
        String identifier = generateIdentifier(optionalMemberId);
        String cacheKey = entityType + ":" + entityId + ":" + identifier;

        // 현재 시간 가져오기
        long currentTime = System.currentTimeMillis();

        // 로컬 캐시에서 마지막 조회 시간 확인
        Long lastViewTime = localCache.getIfPresent(cacheKey);

        // 3초 이내 재요청 체크
        if (lastViewTime != null && (currentTime - lastViewTime < MIN_VIEW_INTERVAL_MS)) {
            // 로컬 캐시 시간 업데이트
            localCache.put(cacheKey, currentTime);
            return false; // 3초 내 재요청으로 조회수 증가하지 않음
        }

        // 로컬 캐시에 현재 시간 저장
        localCache.put(cacheKey, currentTime);

        // 24시간 Redis 로직은 제거하고 항상 true 반환 (새로운 조회로 인정)
        return true;
    }

    private String generateIdentifier(Optional<Long> optionalMemberId) {
        if (optionalMemberId.isPresent()) {
            return "m" + optionalMemberId.get();
        } else {
            String ip = getClientIp();

            // 보안 향상: IP 주소 해싱으로 개인정보 보호
            String hashedIp = DigestUtils.sha256Hex(ip);
            return "g" + hashedIp.substring(0, 10); // 해시 일부만 사용
        }
    }

    /** 클라이언트 IP 주소 가져오기 */
    private String getClientIp() {
        String headerIp = httpServletRequest.getHeader("X-Forwarded-For");

        if (headerIp != null && !headerIp.isEmpty() && !headerIp.equalsIgnoreCase("unknown")) {
            return headerIp.split(",")[0].trim();
        }

        return httpServletRequest.getRemoteAddr();
    }
}
