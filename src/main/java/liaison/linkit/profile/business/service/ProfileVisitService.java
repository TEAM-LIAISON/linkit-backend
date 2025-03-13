package liaison.linkit.profile.business.service;

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

    public ProfileVisitResponseDTO.ProfileVisitInformation getProfileVisitInforms(
            final Long memberId, final String emailId) {
        return ProfileVisitResponseDTO.ProfileVisitInformation.builder().build();
    }
}
