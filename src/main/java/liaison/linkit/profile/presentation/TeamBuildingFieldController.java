package liaison.linkit.profile.presentation;

import jakarta.validation.Valid;
import liaison.linkit.auth.Auth;
import liaison.linkit.auth.MemberOnly;
import liaison.linkit.auth.domain.Accessor;
import liaison.linkit.profile.dto.request.teamBuilding.ProfileTeamBuildingCreateRequest;
import liaison.linkit.profile.service.TeamBuildingFieldService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/private")
@Slf4j
public class TeamBuildingFieldController {
    public final TeamBuildingFieldService teamBuildingFieldService;

    // 1.5.2. 희망 팀빌딩 분야 생성/수정
    @PostMapping("/team_building_field")
    @MemberOnly
    public ResponseEntity<Void> createProfileTeamBuilding(
            @Auth final Accessor accessor,
            @RequestBody @Valid ProfileTeamBuildingCreateRequest profileTeamBuildingCreateRequest
    ) {
        log.info("memberId={}의 희망 팀빌딩 분야 생성/수정 요청이 들어왔습니다.", accessor.getMemberId());
        teamBuildingFieldService.save(accessor.getMemberId(), profileTeamBuildingCreateRequest);
        return ResponseEntity.ok().build();
    }
}
