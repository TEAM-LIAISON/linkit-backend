package liaison.linkit.profile.service;

import liaison.linkit.profile.business.ProfileLicenseMapper;
import liaison.linkit.profile.implement.ProfileLicenseCommandAdapter;
import liaison.linkit.profile.presentation.license.dto.ProfileLicenseResponseDTO.RemoveProfileLicense;
import liaison.linkit.profile.presentation.license.dto.ProfileLicenseResponseDTO.ProfileLicenseItems;
import liaison.linkit.profile.implement.ProfileLicenseQueryAdapter;
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
    public ProfileLicenseItems getProfileLicenseList(final Long memberId) {
        return profileLicenseQueryAdapter.findProfileLicenseListDTO(memberId);
    }

    // 프로필 자격증 삭제 메서드
    public RemoveProfileLicense deleteProfileLicense(final Long profileLicenseId) {
        profileLicenseCommandAdapter.deleteProfileLicenseById(profileLicenseId);
        return profileLicenseMapper.toRemoveProfileLicense(profileLicenseId);
    }

}
