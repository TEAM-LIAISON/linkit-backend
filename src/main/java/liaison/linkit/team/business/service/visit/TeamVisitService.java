package liaison.linkit.team.business.service.visit;

import liaison.linkit.team.business.assembler.team.TeamVisitModalAssembler;
import liaison.linkit.team.presentation.visit.dto.TeamVisitResponseDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class TeamVisitService {
    // assemblers
    private final TeamVisitModalAssembler teamVisitModalAssembler;

    // 프로필 방문자 정보를 조회한다.
    public TeamVisitResponseDTO.TeamVisitInforms getTeamVisitInforms(
            final Long memberId, final String teamCode) {

        return teamVisitModalAssembler.assembleTeamVisitInforms(memberId, teamCode);
    }
}
