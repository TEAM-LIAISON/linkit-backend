package liaison.linkit.visit.infrastructure;

import java.util.Collections;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.script.RedisScript;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class VisitorRedisUtil {

    private final RedisTemplate<String, Object> redisTemplate;
    private static final String VISITOR_KEY_PREFIX = "visitor:";

    // 중복 코드 제거를 위해 Lua 스크립트를 상수로 분리
    private static final String CHECK_AND_SET_SCRIPT =
            "if redis.call('exists', KEYS[1]) == 0 then "
                    + "redis.call('setex', KEYS[1], ARGV[1], 1) "
                    + "return 1 "
                    + "else "
                    + "return 0 "
                    + "end";

    private static final RedisScript<Long> REDIS_SCRIPT =
            RedisScript.of(CHECK_AND_SET_SCRIPT, Long.class);

    /** 팀 방문자 정보 체크 및 설정 */
    public boolean checkAndSetTeamVisitor(
            final String entity,
            final Long visitedTeamId,
            final Long visitorProfileId,
            final String identifier,
            final int expirationHours) {

        if (visitedTeamId == null || visitorProfileId == null) {
            log.warn(
                    "Invalid team visit parameters: visitedTeamId={}, visitorProfileId={}",
                    visitedTeamId,
                    visitorProfileId);
            return false;
        }

        try {
            String key = generateTeamKey(entity, visitedTeamId, visitorProfileId, identifier);

            Long result =
                    redisTemplate.execute(
                            REDIS_SCRIPT,
                            Collections.singletonList(key),
                            expirationHours * 3600); // 시간을 초로 변환

            return result != null && result == 1L;
        } catch (Exception e) {
            log.error(
                    "Error in Redis operation for team visit: visitedTeamId={}, visitorProfileId={}",
                    visitedTeamId,
                    visitorProfileId,
                    e);
            return true; // Redis 오류 시 DB에 저장 가능하도록 true 반환
        }
    }

    /** 프로필 방문자 정보 체크 및 설정 */
    public boolean checkAndSetProfileVisitor(
            final String entity,
            final Long visitedProfileId,
            final Long visitorProfileId,
            final String identifier,
            final int expirationHours) {

        if (visitedProfileId == null || visitorProfileId == null) {
            log.warn(
                    "Invalid profile visit parameters: visitedProfileId={}, visitorProfileId={}",
                    visitedProfileId,
                    visitorProfileId);
            return false;
        }

        try {
            String key = generateProfileKey(entity, visitedProfileId, visitorProfileId, identifier);

            Long result =
                    redisTemplate.execute(
                            REDIS_SCRIPT,
                            Collections.singletonList(key),
                            expirationHours * 3600); // 시간을 초로 변환

            return result != null && result == 1L;
        } catch (Exception e) {
            log.error(
                    "Error in Redis operation for profile visit: visitedProfileId={}, visitorProfileId={}",
                    visitedProfileId,
                    visitorProfileId,
                    e);
            return true; // Redis 오류 시 DB에 저장 가능하도록 true 반환
        }
    }

    /** 팀 방문자 키 생성 */
    private String generateTeamKey(
            String entity,
            final Long visitedTeamId,
            final Long visitorProfileId,
            final String identifier) {

        StringBuilder keyBuilder =
                new StringBuilder(VISITOR_KEY_PREFIX)
                        .append(entity)
                        .append(":")
                        .append(visitedTeamId)
                        .append(":")
                        .append(visitorProfileId);

        // identifier가 null이 아닌 경우에만 추가
        if (identifier != null && !identifier.isEmpty()) {
            keyBuilder.append(":").append(identifier);
        }

        return keyBuilder.toString();
    }

    /** 프로필 방문자 키 생성 */
    private String generateProfileKey(
            String entity,
            final Long visitedProfileId,
            final Long visitorProfileId,
            final String identifier) {

        StringBuilder keyBuilder =
                new StringBuilder(VISITOR_KEY_PREFIX)
                        .append(entity)
                        .append(":")
                        .append(visitedProfileId)
                        .append(":")
                        .append(visitorProfileId);

        // identifier가 null이 아닌 경우에만 추가
        if (identifier != null && !identifier.isEmpty()) {
            keyBuilder.append(":").append(identifier);
        }

        return keyBuilder.toString();
    }
}
