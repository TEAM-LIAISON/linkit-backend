package liaison.linkit.visit.event;

import java.time.LocalDateTime;

import liaison.linkit.profile.implement.profile.ProfileQueryAdapter;
import liaison.linkit.visit.business.service.VisitService;
import liaison.linkit.visit.domain.ProfileVisit;
import liaison.linkit.visit.domain.TeamVisit;
import liaison.linkit.visit.implement.ProfileVisitCommandAdapter;
import liaison.linkit.visit.implement.ProfileVisitQueryAdapter;
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

    private final TeamVisitCommandAdapter teamVisitCommandAdapter;
    private final TeamVisitQueryAdapter teamVisitQueryAdapter;
    private final ProfileQueryAdapter profileQueryAdapter;
    private final ProfileVisitQueryAdapter profileVisitQueryAdapter;
    private final ProfileVisitCommandAdapter profileVisitCommandAdapter;

    @Async // 비동기 처리로 API 응답 지연 방지
    @Transactional
    @EventListener
    public void handleTeamVisitedEvent(TeamVisitedEvent event) {
        visitService.processTeamVisit(
                event.getEntityType(),
                event.getVisitedTeamId(),
                event.getVisitorProfileId(),
                event.getOptionalMemberId());

        // DB에 존재하는지 확인
        if (teamVisitQueryAdapter.existsByVisitedTeamIdAndVisitorProfileId(
                event.getVisitedTeamId(), event.getVisitorProfileId())) {
            // 기존 방문 기록이 있으면 시간 업데이트
            final TeamVisit existingVisit =
                    teamVisitQueryAdapter.getTeamVisitByVisitedTeamIdAndVisitorProfileId(
                            event.getVisitedTeamId(), event.getVisitorProfileId());
            teamVisitCommandAdapter.updateVisitTime(existingVisit.updateVisitTime());
        } else {
            // 첫 방문이면 새 레코드 생성
            teamVisitCommandAdapter.save(
                    TeamVisit.builder()
                            .profile(
                                    profileQueryAdapter.findByMemberId(event.getVisitorProfileId()))
                            .visitedTeamId(event.getVisitedTeamId())
                            .visitTime(LocalDateTime.now())
                            .build());
        }
    }

    @Async // 비동기 처리로 API 응답 지연 방지
    @Transactional
    @EventListener
    public void handleProfileVisitedEvent(ProfileVisitedEvent event) {
        visitService.processProfileVisit(
                event.getEntityType(),
                event.getVisitedProfileId(),
                event.getVisitorProfileId(),
                event.getOptionalMemberId());

        // DB에 존재하는지 확인
        if (profileVisitQueryAdapter.existsByVisitedProfileIdAndVisitorProfileId(
                event.getVisitedProfileId(), event.getVisitorProfileId())) {
            // 기존 방문 기록이 있으면 시간 업데이트
            final ProfileVisit existingVisit =
                    profileVisitQueryAdapter.getProfileVisitByVisitedProfileIdAndVisitorProfileId(
                            event.getVisitedProfileId(), event.getVisitorProfileId());
            profileVisitCommandAdapter.updateVisitTime(existingVisit.updateVisitTime());
        } else {
            // 첫 방문이면 새 레코드 생성
            profileVisitCommandAdapter.save(
                    ProfileVisit.builder()
                            .profile(
                                    profileQueryAdapter.findByMemberId(event.getVisitorProfileId()))
                            .visitedProfileId(event.getVisitedProfileId())
                            .visitTime(LocalDateTime.now())
                            .build());
        }
    }
}
