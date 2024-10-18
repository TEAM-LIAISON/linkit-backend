package liaison.linkit.profile.service;

import liaison.linkit.profile.business.ProfileUrlMapper;
import liaison.linkit.profile.implement.ProfileUrlCommandAdapter;
import liaison.linkit.profile.implement.ProfileUrlQueryAdapter;
import liaison.linkit.profile.presentation.url.dto.ProfileUrlResponseDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class ProfileUrlService {
    private final ProfileUrlQueryAdapter profileUrlQueryAdapter;
    private final ProfileUrlCommandAdapter profileUrlCommandAdapter;
    private final ProfileUrlMapper profileUrlMapper;

    @Transactional(readOnly = true)
    public ProfileUrlResponseDTO.ProfileUrlItems getProfileUrlDetail(final Long memberId) {
        return profileUrlQueryAdapter.getProfileUrlDetail(memberId);
    }
}
