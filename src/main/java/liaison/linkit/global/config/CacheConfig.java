package liaison.linkit.global.config;

import com.github.benmanes.caffeine.cache.Caffeine;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import liaison.linkit.global.type.CacheType;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.caffeine.CaffeineCache;
import org.springframework.cache.support.SimpleCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableCaching
public class CacheConfig {

    @Bean
    public CacheManager cacheManager() {
        SimpleCacheManager cacheManager = new SimpleCacheManager();   // SimpleCacheManager 사용
        // 각 캐시 타입에 대한 설정 적용
        List<CaffeineCache> caches = Arrays.stream(CacheType.values())
            .map(
                cache -> new CaffeineCache(cache.getCacheName(),
                    Caffeine.newBuilder()
                        .expireAfterWrite(cache.getExpiredAfterWrite(), TimeUnit.SECONDS)
                        .maximumSize(cache.getMaximumSize())
                        .build()
                )
            )
            .collect(Collectors.toList());
        cacheManager.setCaches(caches);

        return cacheManager;
    }
}
