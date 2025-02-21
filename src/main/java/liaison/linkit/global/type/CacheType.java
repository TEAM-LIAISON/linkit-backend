package liaison.linkit.global.type;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum CacheType {
    TOP_COMPLETION_PROFILE("top_completion_profile", 12, 100);

    private final String cacheName;
    private final int expiredAfterWrite;
    private final int maximumSize;
}
