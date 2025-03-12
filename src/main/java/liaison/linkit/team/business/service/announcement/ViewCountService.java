package liaison.linkit.team.business.service.announcement;

import java.util.Optional;
import java.util.concurrent.TimeUnit;

import jakarta.servlet.http.HttpServletRequest;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import liaison.linkit.team.infrastructure.ViewCountRedisUtil;
import lombok.RequiredArgsConstructor;
import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ViewCountService {
    private final ViewCountRedisUtil viewCountRedisUtil;
    private final HttpServletRequest httpServletRequest;

    // 조회수 중복 방지 만료 시간 (24시간)
    private static final int VIEW_EXPIRATION_HOURS = 24;

    // 로컬 캐시 추가 (짧은 시간 동안 동일 요청 방지)
    private final Cache<String, Boolean> localCache =
            Caffeine.newBuilder().expireAfterWrite(5, TimeUnit.MINUTES).maximumSize(10000).build();

    @Transactional
    public boolean processView(String entityType, Long entityId, Optional<Long> optionalMemberId) {
        String identifier = generateIdentifier(optionalMemberId);
        String cacheKey = entityType + ":" + entityId + ":" + identifier;

        // 로컬 캐시 확인 (짧은 시간 내 동일 요청 최적화)
        Boolean cachedResult = localCache.getIfPresent(cacheKey);
        if (cachedResult != null && !cachedResult) {
            return false; // 이미 확인한 중복 요청
        }

        boolean isFirstView =
                viewCountRedisUtil.checkAndSetView(
                        entityType, entityId, identifier, VIEW_EXPIRATION_HOURS);

        // 결과 로컬 캐싱
        localCache.put(cacheKey, isFirstView);

        return isFirstView;
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
