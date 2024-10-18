package liaison.linkit.profile.presentation.license.dto;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ProfileLicenseRequestDTO {

    @Getter
    public static class EditProfileLicense {
        private Long profileLicenseId;
        private String licenseName;
        private String licenseInstitution;
        private int acquisitionYear;
        private String licenseDescription;
        private Boolean isLicenseCertified;
        private String licenseCertificationDescription;
    }
}
