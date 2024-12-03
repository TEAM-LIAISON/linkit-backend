package liaison.linkit.profile.presentation.license.dto;

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
    public static class ProfileLicenseItems {
        @Builder.Default
        private List<ProfileLicenseItem> profileLicenseItems = new ArrayList<>();
    }

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ProfileLicenseItem {
        private Long profileLicenseId;
        private String licenseName;
        private String licenseInstitution;
        private String licenseAcquisitionDate;
        private Boolean isLicenseVerified;
        private String licenseDescription;
    }

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ProfileLicenseDetail {
        private Long profileLicenseId;
        private String licenseName;
        private String licenseInstitution;
        private String licenseAcquisitionDate;
        private String licenseDescription;

        // 증명서 및 인증 정보 변수 추가
        private Boolean isLicenseCertified;
        private Boolean isLicenseVerified;
        private String licenseCertificationAttachFileName;
        private String licenseCertificationAttachFilePath;
    }

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class AddProfileLicenseResponse {
        private Long profileLicenseId;
        private String licenseName;
        private String licenseInstitution;
        private String licenseAcquisitionDate;
        private String licenseDescription;
    }

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UpdateProfileLicenseResponse {
        private Long profileLicenseId;
        private String licenseName;
        private String licenseInstitution;
        private String licenseAcquisitionDate;
        private String licenseDescription;
    }

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ProfileLicenseCertificationResponse {
        private Boolean isLicenseCertified;
        private Boolean isLicenseVerified;
        private String licenseCertificationAttachFileName;
        private String licenseCertificationAttachFilePath;
    }

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class RemoveProfileLicenseCertificationResponse {
        private Long profileLicenseId;
    }

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class RemoveProfileLicenseResponse {
        private Long profileLicenseId;
    }
}
