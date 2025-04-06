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

    // 프로필 검색 관련 캐시 추가
    PROFILE_EXCLUDING_IDS_CACHE("profileExcludingIdsCache", 300, 50),
    PROFILE_FILTERING_CACHE("profileFilteringCache", 300, 50),

    // 프로필 정보 관련 캐시 추가
    REGION_DETAIL_CACHE("regionDetailCache", 3600, 200),
    PROFILE_CURRENT_STATE_CACHE("profileCurrentStateCache", 3600, 200),
    PROFILE_POSITION_CACHE("profilePositionCache", 3600, 200),
    PROFILE_TEAM_CACHE("profileTeamCache", 3600, 200),
    PROFILE_SCRAP_CACHE("profileScrapCache", 3600, 200),
    PROFILE_SCRAP_COUNT_CACHE("profileScrapCountCache", 3600, 200),
    PROFILE_INFORM_MENU_CACHE("profileInformMenuCache", 3600, 200);

    private final String cacheName;
    private final int expiredAfterWrite;
    private final int maximumSize;
}
