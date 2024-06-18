package liaison.linkit.team.presentation;

import jakarta.validation.Valid;
import liaison.linkit.auth.Auth;
import liaison.linkit.auth.MemberOnly;
import liaison.linkit.auth.domain.Accessor;
import liaison.linkit.team.dto.request.miniprofile.TeamMiniProfileCreateRequest;
import liaison.linkit.team.service.TeamMiniProfileService;
import lombok.RequiredArgsConstructor;
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
public class TeamMiniProfileController {

    private final TeamMiniProfileService teamMiniProfileService;

    @PostMapping
    @MemberOnly
    public ResponseEntity<Void> createTeamMiniProfile(
            @Auth final Accessor accessor,
            @RequestPart @Valid TeamMiniProfileCreateRequest teamMiniProfileCreateRequest,
            @RequestPart MultipartFile teamMiniProfileImage
    ) {
        teamMiniProfileService.save(accessor.getMemberId(), teamMiniProfileCreateRequest, teamMiniProfileImage);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }
}
