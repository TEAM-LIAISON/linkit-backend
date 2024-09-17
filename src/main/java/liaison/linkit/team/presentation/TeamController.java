package liaison.linkit.team.presentation;

import liaison.linkit.auth.Auth;
import liaison.linkit.auth.MemberOnly;
import liaison.linkit.auth.domain.Accessor;
import liaison.linkit.team.service.TeamService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static org.springframework.http.HttpStatus.CREATED;

@RestController
@RequiredArgsConstructor
@RequestMapping
@Slf4j
public class TeamController {

    private final TeamService teamService;

    // [3.0.0] 회원이 팀을 생성한다.
    @PostMapping("/team")
    @MemberOnly
    public ResponseEntity<?> createNewTeam(
            @Auth final Accessor accessor
    ) {
        teamService.createNewTeam(accessor);
        return ResponseEntity.status(CREATED).build();
    }
}
