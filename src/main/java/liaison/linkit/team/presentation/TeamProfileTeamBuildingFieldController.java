package liaison.linkit.team.presentation;

import liaison.linkit.team.service.TeamProfileTeamBuildingFieldService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequiredArgsConstructor
@RequestMapping("/team_profile_team_building_field")
public class TeamProfileTeamBuildingFieldController {

    private final TeamProfileTeamBuildingFieldService teamProfileTeamBuildingFieldService;

    // 희망 팀빌딩 항목 전체 조회
//    @GetMapping
//    @MemberOnly
//    public ResponseEntity<TeamProfileTeamBuildingFieldResponse> getTeamProfileTeamBuildingList(
//            @Auth final Accessor accessor
//    ) {
//        teamProfileTeamBuildingFieldService.validateTeamProfileTeamBuildingFieldByMember(accessor.getMemberId());
//        final TeamProfileTeamBuildingFieldResponse teamProfileTeamBuildingFieldResponse =
//                teamProfileTeamBuildingFieldService.getAllTeamProfileTeamBuildingFields(accessor.getMemberId());
//        return ResponseEntity.ok().body(teamProfileTeamBuildingFieldResponse);
//    }
}
