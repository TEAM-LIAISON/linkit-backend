package liaison.linkit.visit.business.service;

import liaison.linkit.visit.business.assembler.VisitModalAssembler;
import liaison.linkit.visit.presentation.dto.VisitResponseDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class VisitService {
    // assemblers
    private final VisitModalAssembler visitModalAssembler;

    // 프로필 방문자 정보를 조회한다.
    public VisitResponseDTO.VisitInforms getProfileVisitInforms(final Long memberId) {

        return visitModalAssembler.assembleProfileVisitInforms(memberId);
    }

    // 프로필 방문자 정보를 조회한다.
    public VisitResponseDTO.VisitInforms getTeamVisitInforms(
            final Long memberId, final String teamCode) {

        return visitModalAssembler.assembleTeamVisitInforms(memberId, teamCode);
    }
}
