package liaison.linkit.team.presentation;

import jakarta.validation.Valid;
import liaison.linkit.auth.Auth;
import liaison.linkit.auth.MemberOnly;
import liaison.linkit.auth.domain.Accessor;
import liaison.linkit.team.dto.request.memberIntroduction.TeamMemberIntroductionCreateRequest;
import liaison.linkit.team.service.TeamMemberIntroductionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping
public class TeamMemberIntroductionController {

    private final TeamMemberIntroductionService teamMemberIntroductionService;

//    @PostMapping("/team/member")

    @PostMapping("/team/members")
    @MemberOnly
    public ResponseEntity<Void> createTeamMemberIntroduction(
            @Auth final Accessor accessor,
            @RequestBody @Valid List<TeamMemberIntroductionCreateRequest> teamMemberIntroductionCreateRequests
    ) {
        teamMemberIntroductionService.saveTeamMember(accessor.getMemberId(), teamMemberIntroductionCreateRequests);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @DeleteMapping("/team/members/{teamMemberIntroductionId}")
    @MemberOnly
    public ResponseEntity<Void> deleteTeamMemberIntroduction(
            @Auth final Accessor accessor,
            @PathVariable final Long teamMemberIntroductionId
    ) {
        teamMemberIntroductionService.validateTeamMemberIntroductionByMember(accessor.getMemberId());
        teamMemberIntroductionService.deleteTeamMemberIntroduction(accessor.getMemberId(), teamMemberIntroductionId);
        return ResponseEntity.noContent().build();
    }
}

