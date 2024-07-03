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
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/team/members")
public class TeamMemberIntroductionController {

    private final TeamMemberIntroductionService teamMemberIntroductionService;

    @PostMapping
    @MemberOnly
    public ResponseEntity<Void> createTeamMemberIntroduction(
            @Auth final Accessor accessor,
            @RequestBody @Valid List<TeamMemberIntroductionCreateRequest> teamMemberIntroductionCreateRequests
    ) {
        teamMemberIntroductionService.saveTeamMember(accessor.getMemberId(), teamMemberIntroductionCreateRequests);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }
}
