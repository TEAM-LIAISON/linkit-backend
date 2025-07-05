package liaison.linkit.visit.business.service;

import java.util.Optional;
import java.util.concurrent.TimeUnit;

import jakarta.servlet.http.HttpServletRequest;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import liaison.linkit.visit.business.assembler.VisitModalAssembler;
import liaison.linkit.visit.infrastructure.VisitorRedisUtil;
import liaison.linkit.visit.presentation.dto.VisitResponseDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class VisitService {
    // assemblers
    private final VisitModalAssembler visitModalAssembler;

    private final HttpServletRequest httpServletRequest;

    private final VisitorRedisUtil visitorRedisUtil;

    private static final int VIEW_EXPIRATION_HOURS = 24;

    // 로컬 캐시 추가 (짧은 시간 동안 동일 요청 방지)
    private final Cache<String, Boolean> localCache =
            Caffeine.newBuilder().expireAfterWrite(5, TimeUnit.MINUTES).maximumSize(10000).build();

    @Transactional
    public boolean processTeamVisit(
            String entityType,
            Long visitedTeamId,
            Long visitorProfileId,
            Optional<Long> optionalMemberId) {
        String identifier = generateIdentifier(optionalMemberId);
        String cacheKey =
                entityType + ":" + visitedTeamId + ":" + visitorProfileId + ":" + identifier;

        // 로컬 캐시 확인 (짧은 시간 내 동일 요청 최적화)
        Boolean cachedResult = localCache.getIfPresent(cacheKey);
        if (cachedResult != null && !cachedResult) {
            return false; // 이미 확인한 중복 요청
        }

        boolean isFirstView =
                visitorRedisUtil.checkAndSetTeamVisitor(
                        entityType,
                        visitedTeamId,
                        visitorProfileId,
                        identifier,
                        VIEW_EXPIRATION_HOURS);

        // 결과 로컬 캐싱
        localCache.put(cacheKey, isFirstView);

        return isFirstView;
    }

    @Transactional
    public boolean processProfileVisit(
            String entityType,
            Long visitedProfileId,
            Long visitorProfileId,
            Optional<Long> optionalMemberId) {
        String identifier = generateIdentifier(optionalMemberId);
        String cacheKey =
                entityType + ":" + visitedProfileId + ":" + visitorProfileId + ":" + identifier;

        // 로컬 캐시 확인 (짧은 시간 내 동일 요청 최적화)
        Boolean cachedResult = localCache.getIfPresent(cacheKey);
        if (cachedResult != null && !cachedResult) {
            return false; // 이미 확인한 중복 요청
        }

        boolean isFirstView =
                visitorRedisUtil.checkAndSetProfileVisitor(
                        entityType,
                        visitedProfileId,
                        visitorProfileId,
                        identifier,
                        VIEW_EXPIRATION_HOURS);

        // 결과 로컬 캐싱
        localCache.put(cacheKey, isFirstView);

        return isFirstView;
    }

    // 프로필 방문자 정보를 조회한다.
    public VisitResponseDTO.VisitInforms getProfileVisitInforms(final Long memberId) {

        return visitModalAssembler.assembleProfileVisitInforms(memberId);
    }

    // 프로필 방문자 정보를 조회한다.
    public VisitResponseDTO.VisitInforms getTeamVisitInforms(
            final Long memberId, final String teamCode) {

        return visitModalAssembler.assembleTeamVisitInforms(memberId, teamCode);
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
