package liaison.linkit.team.presentation;

import jakarta.validation.Valid;
import liaison.linkit.auth.Auth;
import liaison.linkit.auth.MemberOnly;
import liaison.linkit.auth.domain.Accessor;
import liaison.linkit.team.dto.request.announcement.TeamMemberAnnouncementRequest;
import liaison.linkit.team.service.TeamMemberAnnouncementService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

// 팀원 공고
@RestController
@RequiredArgsConstructor
@RequestMapping("/team")
public class TeamMemberAnnouncementController {

    private final TeamMemberAnnouncementService teamMemberAnnouncementService;

    @PostMapping("/members/announcements")
    @MemberOnly
    public ResponseEntity<Void> postTeamMemberAnnouncement(
            @Auth final Accessor accessor,
            @RequestBody @Valid List<TeamMemberAnnouncementRequest> teamMemberAnnouncementRequestList
    ) {
        teamMemberAnnouncementService.postAnnouncements(accessor.getMemberId(), teamMemberAnnouncementRequestList);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }
}
