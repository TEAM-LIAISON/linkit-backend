package liaison.linkit.team.presentation;

import jakarta.validation.Valid;
import liaison.linkit.auth.Auth;
import liaison.linkit.auth.MemberOnly;
import liaison.linkit.auth.domain.Accessor;
import liaison.linkit.team.dto.request.DefaultTeamProfileCreateRequest;
import liaison.linkit.team.service.TeamProfileService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/team-profile")
public class TeamProfileController {

    public final TeamProfileService teamProfileService;

    @PostMapping("/default")
    @MemberOnly
    public ResponseEntity<Void> createDefaultTeamProfile(
            @Auth final Accessor accessor,
            @RequestBody @Valid final DefaultTeamProfileCreateRequest defaultTeamProfileCreateRequest
    ) {
//        teamProfileService.saveDefault(accessor.getMemberId(), defaultTeamProfileCreateRequest);
        return ResponseEntity.ok().build();
    }
}
