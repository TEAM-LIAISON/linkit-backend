package liaison.linkit.team.presentation;

import jakarta.validation.Valid;
import liaison.linkit.auth.Auth;
import liaison.linkit.auth.MemberOnly;
import liaison.linkit.auth.domain.Accessor;
import liaison.linkit.team.dto.request.announcement.TeamMemberAnnouncementRequest;
import liaison.linkit.team.service.TeamMemberAnnouncementService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static org.springframework.http.HttpStatus.CREATED;

// 팀원 공고
@RestController
@RequiredArgsConstructor
@RequestMapping("/team")
@Slf4j
public class TeamMemberAnnouncementController {

    private final TeamMemberAnnouncementService teamMemberAnnouncementService;

    // 단일 팀원 공고 생성
    @PostMapping("/member/announcement")
    @MemberOnly
    public ResponseEntity<?> createTeamMemberAnnouncement(
            @Auth final Accessor accessor,
            @RequestBody @Valid TeamMemberAnnouncementRequest teamMemberAnnouncementRequest
    ) {
        log.info("memberId={}의 팀원 공고 생성 요청이 들어왔습니다.", accessor.getMemberId());
        final Long teamMemberAnnouncementId = teamMemberAnnouncementService.saveAnnouncement(accessor.getMemberId(), teamMemberAnnouncementRequest);
        return ResponseEntity.status(CREATED).body(teamMemberAnnouncementId);
    }

    // 단일 팀원 공고 수정
    @PostMapping("/member/announcement/{teamMemberAnnouncementId}")
    @MemberOnly
    public ResponseEntity<?> updateTeamMemberAnnouncement(
            @Auth final Accessor accessor,
            @PathVariable final Long teamMemberAnnouncementId,
            @RequestBody @Valid TeamMemberAnnouncementRequest teamMemberAnnouncementRequest
    ) {
        log.info("memberId={}의 teamMemberAnnouncementId={} 수정 요청이 발생하였습니다.", accessor.getMemberId(), teamMemberAnnouncementId);
        final Long updatedTeamMemberAnnouncementId = teamMemberAnnouncementService.updateTeamMemberAnnouncement(teamMemberAnnouncementId, teamMemberAnnouncementRequest);
        return ResponseEntity.ok().body(updatedTeamMemberAnnouncementId);
    }

    // 사용하지 않음
    @PostMapping("/members/announcements")
    @MemberOnly
    public ResponseEntity<Void> createTeamMemberAnnouncements(
            @Auth final Accessor accessor,
            @RequestBody @Valid List<TeamMemberAnnouncementRequest> teamMemberAnnouncementRequestList
    ) {
        teamMemberAnnouncementService.saveAnnouncements(accessor.getMemberId(), teamMemberAnnouncementRequestList);
        return ResponseEntity.status(CREATED).build();
    }

    @DeleteMapping("/members/announcements/{teamMemberAnnouncementId}")
    @MemberOnly
    public ResponseEntity<Void> deleteTeamMemberAnnouncement(
            @Auth final Accessor accessor,
            @PathVariable final Long teamMemberAnnouncementId
    ) {
        teamMemberAnnouncementService.validateTeamMemberAnnouncement(accessor.getMemberId());
        teamMemberAnnouncementService.deleteTeamMemberAnnouncement(accessor.getMemberId(), teamMemberAnnouncementId);
        return ResponseEntity.noContent().build();
    }
}
