package liaison.linkit.profile.presentation;

import jakarta.validation.Valid;
import liaison.linkit.auth.Auth;
import liaison.linkit.auth.MemberOnly;
import liaison.linkit.auth.domain.Accessor;
import liaison.linkit.profile.dto.request.teamBuilding.ProfileTeamBuildingCreateRequest;
import liaison.linkit.profile.dto.request.teamBuilding.ProfileTeamBuildingUpdateRequest;
import liaison.linkit.profile.dto.response.ProfileTeamBuildingFieldResponse;
import liaison.linkit.profile.service.ProfileTeamBuildingFieldService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/profile_team_building_field")
@Slf4j
public class ProfileTeamBuildingFieldController {

    private final ProfileTeamBuildingFieldService profileTeamBuildingFieldService;

    // 희망 팀빌딩 항목 생성 (기존에 존재하는 경우, 전체 삭제 이후 다시 기입)
    @PostMapping
    @MemberOnly
    public ResponseEntity<Void> createProfileTeamBuilding(
            @Auth final Accessor accessor,
            @RequestBody @Valid ProfileTeamBuildingCreateRequest profileTeamBuildingCreateRequest
    ) {
        log.info("memberId={}의 희망 팀빌딩 분야 생성 요청이 들어왔습니다.", accessor.getMemberId());
        profileTeamBuildingFieldService.save(accessor.getMemberId(), profileTeamBuildingCreateRequest);
        return ResponseEntity.ok().build();
    }

    // 희망 팀빌딩 항목 전체 조회
    @GetMapping
    @MemberOnly
    public ResponseEntity<ProfileTeamBuildingFieldResponse> getProfileTeamBuildingList(
            @Auth final Accessor accessor
    ) {
        profileTeamBuildingFieldService.validateProfileTeamBuildingFieldByMember(accessor.getMemberId());
        final ProfileTeamBuildingFieldResponse profileTeamBuildingFieldResponses = profileTeamBuildingFieldService.getAllProfileTeamBuildings(accessor.getMemberId());
        return ResponseEntity.ok().body(profileTeamBuildingFieldResponses);
    }

    // 수정 컨트롤러
    @PutMapping
    @MemberOnly
    public ResponseEntity<ProfileTeamBuildingFieldResponse> updateProfileTeamBuildingField(
            @Auth final Accessor accessor,
            @RequestBody @Valid final ProfileTeamBuildingUpdateRequest updateRequest
    ) {
        profileTeamBuildingFieldService.validateProfileTeamBuildingFieldByMember(accessor.getMemberId());
        ProfileTeamBuildingFieldResponse profileTeamBuildingFieldResponse =
                profileTeamBuildingFieldService.update(accessor.getMemberId(), updateRequest);
        return ResponseEntity.ok().body(profileTeamBuildingFieldResponse);
    }

}
