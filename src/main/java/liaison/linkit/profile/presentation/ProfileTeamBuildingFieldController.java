package liaison.linkit.profile.presentation;

import jakarta.validation.Valid;
import liaison.linkit.auth.Auth;
import liaison.linkit.auth.MemberOnly;
import liaison.linkit.auth.domain.Accessor;
import liaison.linkit.profile.dto.request.ProfileTeamBuildingCreateRequest;
import liaison.linkit.profile.dto.response.ProfileTeamBuildingResponse;
import liaison.linkit.profile.service.ProfileTeamBuildingFieldService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/profile_team_building_field")
public class ProfileTeamBuildingFieldController {

    private final ProfileTeamBuildingFieldService profileTeamBuildingFieldService;

    // 희망 팀빌딩 항목 전체 조회
    @GetMapping
    @MemberOnly
    public ResponseEntity<ProfileTeamBuildingResponse> getProfileTeamBuildingList(@Auth final Accessor accessor) {
        profileTeamBuildingFieldService.validateProfileTeamBuildingFieldByMember(accessor.getMemberId());
        final ProfileTeamBuildingResponse profileTeamBuildingResponses = profileTeamBuildingFieldService.getAllProfileTeamBuildings(accessor.getMemberId());
        return ResponseEntity.ok().body(profileTeamBuildingResponses);
    }

    // 희망 팀빌딩 항목 등록 (여러개 등록 가능해야함)
    @PostMapping
    @MemberOnly
    public ResponseEntity<Void> createProfileTeamBuilding(
            @Auth final Accessor accessor,
            @RequestBody @Valid ProfileTeamBuildingCreateRequest profileTeamBuildingCreateRequest
    ) {
        profileTeamBuildingFieldService.save(accessor.getMemberId(), profileTeamBuildingCreateRequest);
        return ResponseEntity.ok().build();
    }

}
