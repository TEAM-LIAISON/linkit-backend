package liaison.linkit.team.infrastructure;

import java.util.Collections;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.script.RedisScript;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ViewCountRedisUtil {

    private final RedisTemplate<String, Object> redisTemplate;
    private static final String KEY_PREFIX = "view:";

    public boolean checkAndSetView(String entity, Long id, String identifier, int expirationHours) {
        String key = generateKey(entity, id, identifier);

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
    private String generateKey(String entity, Long id, String identifier) {
        return KEY_PREFIX + entity + ":" + id + ":" + identifier;
    }
}
