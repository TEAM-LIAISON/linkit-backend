package liaison.linkit.team.presentation;

import jakarta.validation.Valid;
import liaison.linkit.auth.Auth;
import liaison.linkit.auth.MemberOnly;
import liaison.linkit.auth.domain.Accessor;
import liaison.linkit.team.dto.request.miniprofile.TeamMiniProfileCreateRequest;
import liaison.linkit.team.service.TeamMiniProfileService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequiredArgsConstructor
@RequestMapping("/team/mini-profile")
@Slf4j
public class TeamMiniProfileController {

    private final TeamMiniProfileService teamMiniProfileService;

    @PostMapping
    @MemberOnly
    public ResponseEntity<Void> createTeamMiniProfile(
            @Auth final Accessor accessor,
            @RequestPart @Valid TeamMiniProfileCreateRequest teamMiniProfileCreateRequest,
            @RequestPart(required = false) MultipartFile teamMiniProfileImage
    ) {
        log.info("팀 미니 프로필 생성 메서드가 실행됩니다. part 1");
        if (teamMiniProfileService.getIsTeamMiniProfile(accessor.getMemberId())) {          // 존재한다면
            teamMiniProfileService.updateTeamMiniProfile(accessor.getMemberId(), teamMiniProfileCreateRequest, teamMiniProfileImage);
            return ResponseEntity.status(HttpStatus.CREATED).build();
        } else {                                                                            // 온보딩에서 생성하지 않은 사람
            teamMiniProfileService.saveNewTeamMiniProfile(accessor.getMemberId(), teamMiniProfileCreateRequest, teamMiniProfileImage);
            return ResponseEntity.status(HttpStatus.CREATED).build();
        }

    }
}
