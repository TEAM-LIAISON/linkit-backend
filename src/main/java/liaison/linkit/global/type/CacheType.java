package liaison.linkit.global.type;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum CacheType {
    TOP_COMPLETION_PROFILES("topCompletionProfiles", 60000, 100),
    TOP_VENTURE_TEAMS("topVentureTeams", 60000, 100),
    SUPPORT_PROJECT_TEAMS("supportProjectTeams", 60000, 100),
    HOT_ANNOUNCEMENTS("hotAnnouncements", 60000, 100),

    HOME_TOP_PROFILES("homeTopProfiles", 60000, 100),
    HOME_TOP_TEAMS("homeTopTeams", 60000, 100),
    HOME_TOP_ANNOUNCEMENTS("homeTopAnnouncements", 60000, 100),

    // 프로필 검색 관련 캐시만 유지하고 나머지는 제거
    PROFILE_EXCLUDING_IDS_CACHE("profileExcludingIdsCache", 300, 50),
    PROFILE_FILTERING_CACHE("profileFilteringCache", 300, 50);

    private final String cacheName;
    private final int expiredAfterWrite;
    private final int maximumSize;
}
