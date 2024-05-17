package liaison.linkit.team.presentation;

import liaison.linkit.team.service.TeamMiniProfileService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/team/mini-profile")
public class TeamMiniProfileController {

    private final TeamMiniProfileService teamMiniProfileService;

//    @PostMapping
//    @MemberOnly
//    public ResponseEntity<Void> createTeamMiniProfile(
//            @Auth final Accessor accessor,
//            @RequestBody @Valid TeamMiniProfileCreateRequest teamMiniProfileCreateRequest
//    ) {
//        teamMiniProfileService.save(accessor.getMemberId(), teamMiniProfileCreateRequest);
//        return ResponseEntity.status(HttpStatus.CREATED).build();
//    }
//
//    @GetMapping
//    @MemberOnly
//    public ResponseEntity<TeamMiniProfileResponse> getTeamMiniProfile(
//            @Auth final Accessor accessor
//    ) {
//        Long teamMiniProfileId = teamMiniProfileService.validateTeamMiniProfileByMember(accessor.getMemberId());
//        final TeamMiniProfileResponse teamMiniProfileResponse = teamMiniProfileService.getTeamMiniProfileDetail(teamMiniProfileId);
//        return ResponseEntity.ok().body(teamMiniProfileResponse);
//    }

}
