package liaison.linkit.visit.implement;

import liaison.linkit.common.annotation.Adapter;
import liaison.linkit.visit.domain.repository.TeamVisitRepository;
import lombok.RequiredArgsConstructor;

@Adapter
@RequiredArgsConstructor
public class TeamVisitCommandAdapter {
    private final TeamVisitRepository teamVisitRepository;
}
