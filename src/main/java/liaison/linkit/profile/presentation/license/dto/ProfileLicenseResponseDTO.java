package liaison.linkit.profile.presentation.license.dto;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ProfileLicenseResponseDTO {

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ProfileLicenseItem {
        private Long profileLicenseId;
        private String licenseName;
        private String licenseInstitution;
        private int acquisitionYear;
        private String licenseDescription;
        private Boolean isLicenseCertified;
        private String licenseCertificationAttachFileName;
        private String licenseCertificationAttachFileUrl;
        private String licenseCertificationDescription;
    }

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ProfileLicenseItems {
        @Builder.Default
        private List<ProfileLicenseItem> profileLicenseItems = new ArrayList<>();
    }

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class RemoveProfileLicense {
        private Long profileLicenseId;
        private LocalDateTime deletedAt;
    }
}
