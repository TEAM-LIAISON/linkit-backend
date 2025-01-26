package liaison.linkit.profile.presentation.education.dto;

import java.util.ArrayList;
import java.util.List;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ProfileEducationResponseDTO {

    // 전체 조회에 보이는 항목 (이름, 훈격, 수상시기)
    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ProfileEducationItem {
        private Long profileEducationId;
        private String universityName;
        private String majorName;
        private String admissionYear;
        private String graduationYear;
        private Boolean isAttendUniversity;
        private Boolean isEducationVerified;
        private String educationDescription;
    }

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ProfileEducationItems {
        @Builder.Default
        private List<ProfileEducationResponseDTO.ProfileEducationItem> profileEducationItems = new ArrayList<>();
    }

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ProfileEducationDetail {
        private Long profileEducationId;
        private String universityName;
        private String majorName;
        private String admissionYear;
        private String graduationYear;
        private Boolean isAttendUniversity;
        private String educationDescription;

        // 증명서 및 인증 정보
        private Boolean isEducationCertified;
        private Boolean isEducationVerified;
        private String educationCertificationAttachFileName;
        private String educationCertificationAttachFilePath;
    }

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class AddProfileEducationResponse {
        private Long profileEducationId;
        private String universityName;
        private String majorName;
        private String admissionYear;
        private String graduationYear;
        private Boolean isAttendUniversity;
        private String educationDescription;
    }

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UpdateProfileEducationResponse {
        private Long profileEducationId;
        private String universityName;
        private String majorName;
        private String admissionYear;
        private String graduationYear;
        private Boolean isAttendUniversity;
        private String educationDescription;
    }

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ProfileEducationCertificationResponse {
        private Boolean isEducationCertified;
        private Boolean isEducationVerified;
        private String educationCertificationAttachFileName;
        private String educationCertificationAttachFilePath;
    }

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class RemoveProfileEducationCertificationResponse {
        private Long profileEducationId;
    }

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class RemoveProfileEducationResponse {
        private Long profileEducationId;
    }
}
