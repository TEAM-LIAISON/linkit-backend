package liaison.linkit.profile.presentation.license.dto;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ProfileLicenseRequestDTO {
    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class AddProfileLicenseRequest {
        private String licenseName;
        private String licenseInstitution;
        private String licenseAcquisitionDate;
        private String licenseDescription;
    }

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UpdateProfileLicenseRequest {
        private String licenseName;
        private String licenseInstitution;
        private String licenseAcquisitionDate;
        private String licenseDescription;
    }
}
