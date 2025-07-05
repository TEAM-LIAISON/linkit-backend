package liaison.linkit.team.event;

import liaison.linkit.team.business.service.announcement.ViewCountService;
import liaison.linkit.team.implement.announcement.TeamMemberAnnouncementCommandAdapter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

// 3. 이벤트 리스너
@Component
@RequiredArgsConstructor
public class AnnouncementViewCountListener {
    private final ViewCountService viewCountService;
    private final TeamMemberAnnouncementCommandAdapter teamMemberAnnouncementCommandAdapter;

    @Async // 비동기 처리로 API 응답 지연 방지
    @Transactional
    @EventListener
    public void handleAnnouncementViewedEvent(AnnouncementViewedEvent event) {
        boolean isNewView =
                viewCountService.processView(
                        event.getEntityType(), event.getAnnouncementId(), event.getMemberId());

        if (isNewView) {
            teamMemberAnnouncementCommandAdapter.incrementViewCount(event.getAnnouncementId());
        }
    }
}
