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
    HOME_TOP_ANNOUNCEMENTS("homeTopAnnouncements", 60000, 100);

    private final String cacheName;
    private final int expiredAfterWrite;
    private final int maximumSize;
}
