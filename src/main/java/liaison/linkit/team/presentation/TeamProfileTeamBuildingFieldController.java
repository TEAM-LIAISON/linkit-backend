package liaison.linkit.team.presentation;

import jakarta.validation.Valid;
import liaison.linkit.auth.Auth;
import liaison.linkit.auth.MemberOnly;
import liaison.linkit.auth.domain.Accessor;
import liaison.linkit.team.dto.request.TeamProfileTeamBuildingFieldCreateRequest;
import liaison.linkit.team.dto.response.TeamProfileTeamBuildingFieldResponse;
import liaison.linkit.team.service.TeamProfileTeamBuildingFieldService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequiredArgsConstructor
@RequestMapping("/team_profile_team_building_field")
public class TeamProfileTeamBuildingFieldController {

    private final TeamProfileTeamBuildingFieldService teamProfileTeamBuildingFieldService;

    // 희망 팀빌딩 항목 전체 조회
    @GetMapping
    @MemberOnly
    public ResponseEntity<TeamProfileTeamBuildingFieldResponse> getTeamProfileTeamBuildingList(
            @Auth final Accessor accessor
    ) {
        teamProfileTeamBuildingFieldService.validateTeamProfileTeamBuildingFieldByMember(accessor.getMemberId());
        final TeamProfileTeamBuildingFieldResponse teamProfileTeamBuildingFieldResponse =
                teamProfileTeamBuildingFieldService.getAllTeamProfileTeamBuildingFields(accessor.getMemberId());
        return ResponseEntity.ok().body(teamProfileTeamBuildingFieldResponse);
    }

    @PostMapping
    @MemberOnly
    public ResponseEntity<Void> createTeamProfileTeamBuilding(
            @Auth final Accessor accessor,
            @RequestBody @Valid TeamProfileTeamBuildingFieldCreateRequest teamProfileTeamBuildingFieldCreateRequest
    ) {
        teamProfileTeamBuildingFieldService.save(accessor.getMemberId(), teamProfileTeamBuildingFieldCreateRequest);
        return ResponseEntity.ok().build();
    }
}
