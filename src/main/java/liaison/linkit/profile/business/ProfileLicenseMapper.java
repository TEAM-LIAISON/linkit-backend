package liaison.linkit.profile.business;

import java.time.LocalDateTime;
import liaison.linkit.common.annotation.Mapper;
import liaison.linkit.profile.presentation.license.dto.ProfileLicenseResponseDTO;

@Mapper
public class ProfileLicenseMapper {

    // 삭제 응답 매핑
    public ProfileLicenseResponseDTO.RemoveProfileLicense toRemoveProfileLicense(final Long profileLicenseId) {
        return ProfileLicenseResponseDTO.RemoveProfileLicense.builder()
                .profileLicenseId(profileLicenseId)
                .deletedAt(LocalDateTime.now())
                .build();
    }
}
