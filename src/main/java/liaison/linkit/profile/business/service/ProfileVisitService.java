package liaison.linkit.profile.business.service;

import liaison.linkit.profile.business.assembler.ProfileVisitModalAssembler;
import liaison.linkit.profile.presentation.visit.dto.ProfileVisitResponseDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class ProfileVisitService {

    // assemblers
    private final ProfileVisitModalAssembler profileVisitModalAssembler;

    // 프로필 방문자 정보를 조회한다.
    public ProfileVisitResponseDTO.ProfileVisitInforms getProfileVisitInforms(final Long memberId) {

        return profileVisitModalAssembler.assembleProfileVisitInforms(memberId);
    }
}
