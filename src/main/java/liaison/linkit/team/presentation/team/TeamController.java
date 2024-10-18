package liaison.linkit.team.presentation.team;

import liaison.linkit.team.service.TeamService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping
@Slf4j
public class TeamController {

    private final TeamService teamService;
}
