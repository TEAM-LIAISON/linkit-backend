package liaison.linkit.team.presentation;

import jakarta.validation.Valid;
import liaison.linkit.auth.Auth;
import liaison.linkit.auth.MemberOnly;
import liaison.linkit.auth.domain.Accessor;
import liaison.linkit.global.exception.BadRequestException;
import liaison.linkit.team.dto.request.TeamProfileTeamBuildingFieldCreateRequest;
import liaison.linkit.team.dto.response.TeamProfileTeamBuildingFieldResponse;
import liaison.linkit.team.service.TeamProfileTeamBuildingFieldService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static liaison.linkit.global.exception.ExceptionCode.HAVE_TO_INPUT_PRIVATE_ATTACH_URL;

@RestController
@RequiredArgsConstructor
@RequestMapping("/team/team_building_field")
public class TeamProfileTeamBuildingFieldController {

    private final TeamProfileTeamBuildingFieldService teamProfileTeamBuildingFieldService;

    // 희망 팀빌딩 항목 전체 등록
    @PostMapping
    @MemberOnly
    public ResponseEntity<Void> createTeamProfileTeamBuilding(
            @Auth final Accessor accessor,
            @RequestBody @Valid TeamProfileTeamBuildingFieldCreateRequest teamProfileTeamBuildingFieldCreateRequest
    ) {
        if (teamProfileTeamBuildingFieldCreateRequest.getTeamBuildingFieldNames().isEmpty()) {
            throw new BadRequestException(HAVE_TO_INPUT_PRIVATE_ATTACH_URL);
        }

        teamProfileTeamBuildingFieldService.saveTeamBuildingField(accessor.getMemberId(), teamProfileTeamBuildingFieldCreateRequest.getTeamBuildingFieldNames());
        return ResponseEntity.ok().build();
    }

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


}
