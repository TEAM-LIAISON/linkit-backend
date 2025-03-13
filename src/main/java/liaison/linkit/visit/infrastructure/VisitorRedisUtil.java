package liaison.linkit.visit.infrastructure;

import java.util.Collections;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.script.RedisScript;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class VisitorRedisUtil {

    private final RedisTemplate<String, Object> redisTemplate;
    private static final String VISITOR_KEY_PREFIX = "visitor:";

    public boolean checkAndSetTeamVisitor(
            final String entity,
            final Long visitedTeamId,
            final Long visitorProfileId,
            final String identifier,
            final int expirationHours) {
        String key = generateTeamKey(entity, visitedTeamId, visitorProfileId, identifier);

        // 원자적 연산으로 중복 체크와 설정을 동시에 처리
        String script =
                "if redis.call('exists', KEYS[1]) == 0 then "
                        + "redis.call('setex', KEYS[1], ARGV[1], 1) "
                        + "return 1 "
                        + "else "
                        + "return 0 "
                        + "end";

        RedisScript<Long> redisScript = RedisScript.of(script, Long.class);
        Long result =
                redisTemplate.execute(
                        redisScript,
                        Collections.singletonList(key),
                        expirationHours * 3600); // 시간을 초로 변환

        return result == 1L;
    }

    public boolean checkAndSetProfileVisitor(
            final String entity,
            final Long visitedProfileId,
            final Long visitorProfileId,
            final String identifier,
            final int expirationHours) {
        String key = generateProfileKey(entity, visitedProfileId, visitorProfileId, identifier);

        // 원자적 연산으로 중복 체크와 설정을 동시에 처리
        String script =
                "if redis.call('exists', KEYS[1]) == 0 then "
                        + "redis.call('setex', KEYS[1], ARGV[1], 1) "
                        + "return 1 "
                        + "else "
                        + "return 0 "
                        + "end";

        RedisScript<Long> redisScript = RedisScript.of(script, Long.class);
        Long result =
                redisTemplate.execute(
                        redisScript,
                        Collections.singletonList(key),
                        expirationHours * 3600); // 시간을 초로 변환

        return result == 1L;
    }

    /** 키 생성 */
    private String generateTeamKey(
            String entity,
            final Long visitedTeamId,
            final Long visitorProfileId,
            final String identifier) {
        return VISITOR_KEY_PREFIX
                + entity
                + ":"
                + visitedTeamId
                + ":"
                + visitorProfileId
                + ":"
                + identifier;
    }

    /** 키 생성 */
    private String generateProfileKey(
            String entity,
            final Long visitedProfileId,
            final Long visitorProfileId,
            final String identifier) {
        return VISITOR_KEY_PREFIX
                + entity
                + ":"
                + visitedProfileId
                + ":"
                + visitorProfileId
                + ":"
                + identifier;
    }
}
