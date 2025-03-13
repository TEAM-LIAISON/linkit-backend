package liaison.linkit.visit.event;

import java.time.LocalDateTime;

import liaison.linkit.profile.implement.profile.ProfileQueryAdapter;
import liaison.linkit.visit.business.service.VisitService;
import liaison.linkit.visit.domain.TeamVisit;
import liaison.linkit.visit.implement.ProfileVisitCommandAdapter;
import liaison.linkit.visit.implement.TeamVisitCommandAdapter;
import liaison.linkit.visit.implement.TeamVisitQueryAdapter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

// 3. 이벤트 리스너
@Component
@RequiredArgsConstructor
public class VisitListener {
    private final VisitService visitService;
    private final ProfileVisitCommandAdapter profileVisitCommandAdapter;
    private final TeamVisitCommandAdapter teamVisitCommandAdapter;
    private final TeamVisitQueryAdapter teamVisitQueryAdapter;
    private final ProfileQueryAdapter profileQueryAdapter;

    @Async // 비동기 처리로 API 응답 지연 방지
    @Transactional
    @EventListener
    public void handleTeamVisitedEvent(TeamVisitedEvent event) {
        boolean isNewCacheVisit =
                visitService.processTeamVisit(
                        event.getEntityType(),
                        event.getVisitedTeamId(),
                        event.getVisitorProfileId(),
                        event.getOptionalMemberId());

        if (isNewCacheVisit) {
            boolean isNewDBVisit =
                    !teamVisitQueryAdapter.existsByVisitedTeamIdAndVisitorProfileId(
                            event.getVisitedTeamId(), event.getVisitorProfileId());

            if (isNewDBVisit) {
                teamVisitCommandAdapter.save(
                        TeamVisit.builder()
                                .profile(
                                        profileQueryAdapter.findByMemberId(
                                                event.getVisitorProfileId()))
                                .visitedTeamId(event.getVisitedTeamId())
                                .visitTime(LocalDateTime.now())
                                .build());
            }
        }
    }
}
