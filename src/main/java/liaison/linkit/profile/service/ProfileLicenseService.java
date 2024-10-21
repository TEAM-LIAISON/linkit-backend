package liaison.linkit.profile.service;

import liaison.linkit.profile.business.ProfileLicenseMapper;
import liaison.linkit.profile.implement.ProfileLicenseCommandAdapter;
import liaison.linkit.profile.implement.ProfileLicenseQueryAdapter;
import liaison.linkit.profile.presentation.license.dto.ProfileLicenseResponseDTO.ProfileLicenseItems;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class ProfileLicenseService {

    private final ProfileLicenseQueryAdapter profileLicenseQueryAdapter;
    private final ProfileLicenseCommandAdapter profileLicenseCommandAdapter;
    private final ProfileLicenseMapper profileLicenseMapper;

    // 프로필 자격증 리스트 조회 메서드
    @Transactional(readOnly = true)
    public ProfileLicenseItems getProfileLicenseItems(final Long memberId) {
        return profileLicenseQueryAdapter.getProfileLicenseItems(memberId);
    }

}
