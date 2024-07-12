package liaison.linkit.team.presentation;

import jakarta.validation.Valid;
import liaison.linkit.auth.Auth;
import liaison.linkit.auth.MemberOnly;
import liaison.linkit.auth.domain.Accessor;
import liaison.linkit.team.dto.request.announcement.TeamMemberAnnouncementRequest;
import liaison.linkit.team.service.TeamMemberAnnouncementService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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
    public ResponseEntity<Void> createTeamMemberAnnouncement(
            @Auth final Accessor accessor,
            @RequestBody @Valid TeamMemberAnnouncementRequest teamMemberAnnouncementRequest
    ) {
        teamMemberAnnouncementService.saveAnnouncement(accessor.getMemberId(), teamMemberAnnouncementRequest);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    // 단일 팀원 공고 수정
    @PostMapping("/member/announcement/{teamMemberAnnouncementId}")
    @MemberOnly
    public ResponseEntity<Void> updateTeamMemberAnnouncement(
            @Auth final Accessor accessor,
            @PathVariable final Long teamMemberAnnouncementId,
            @RequestBody @Valid TeamMemberAnnouncementRequest teamMemberAnnouncementRequest
    ) {
        log.info("memberId={}의 팀원 공고 수정 요청이 들어왔습니다.", accessor.getMemberId());
        teamMemberAnnouncementService.updateTeamMemberAnnouncement(teamMemberAnnouncementId, teamMemberAnnouncementRequest);
        return ResponseEntity.ok().build();
    }


    @PostMapping("/members/announcements")
    @MemberOnly
    public ResponseEntity<Void> createTeamMemberAnnouncements(
            @Auth final Accessor accessor,
            @RequestBody @Valid List<TeamMemberAnnouncementRequest> teamMemberAnnouncementRequestList
    ) {
        teamMemberAnnouncementService.saveAnnouncements(accessor.getMemberId(), teamMemberAnnouncementRequestList);
        return ResponseEntity.status(HttpStatus.CREATED).build();
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
