package liaison.linkit.team.business.service.announcement;

import java.util.Optional;
import java.util.concurrent.TimeUnit;

import jakarta.servlet.http.HttpServletRequest;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
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

        // 디버깅을 위한 로그 추가
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
            return "m" + optionalMemberId.get();
        } else {
            // 비로그인 사용자용 식별자 - 여러 헤더 조합 활용
            String ip = getClientIp();
            String userAgent = getUserAgent();

            // IP와 User-Agent 조합으로 더 안정적인 식별자 생성
            String identifier = ip + "|" + userAgent;
            String hashedIdentifier = DigestUtils.sha256Hex(identifier);

            log.debug("Generated non-member identifier from IP: {} and UA hash", ip);
            return "g" + hashedIdentifier.substring(0, 10);
        }
    }

    /** 클라이언트 IP 주소 가져오기 - 다양한 헤더 확인 */
    private String getClientIp() {
        // 다양한 가능한 헤더 확인
        String[] headerNames = {
            "X-Forwarded-For",
            "Proxy-Client-IP",
            "WL-Proxy-Client-IP",
            "HTTP_X_FORWARDED_FOR",
            "HTTP_X_FORWARDED",
            "HTTP_X_CLUSTER_CLIENT_IP",
            "HTTP_CLIENT_IP",
            "HTTP_FORWARDED_FOR",
            "HTTP_FORWARDED",
            "HTTP_VIA",
            "REMOTE_ADDR"
        };

        for (String headerName : headerNames) {
            String headerValue = httpServletRequest.getHeader(headerName);
            if (headerValue != null
                    && !headerValue.isEmpty()
                    && !headerValue.equalsIgnoreCase("unknown")) {
                // 쉼표로 구분된 경우 첫 번째 IP 사용
                if (headerValue.contains(",")) {
                    return headerValue.split(",")[0].trim();
                }
                return headerValue.trim();
            }
        }

        // 모든 헤더가 없거나 유효하지 않은 경우 기본값 사용
        return httpServletRequest.getRemoteAddr();
    }

    /** 사용자 에이전트 정보 가져오기 */
    private String getUserAgent() {
        String userAgent = httpServletRequest.getHeader("User-Agent");
        return userAgent != null ? userAgent : "unknown";
    }
}
