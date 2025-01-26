package liaison.linkit.profile.presentation.education.dto;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ProfileEducationRequestDTO {

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class AddProfileEducationRequest {
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
    public static class UpdateProfileEducationRequest {
        private String universityName;
        private String majorName;
        private String admissionYear;
        private String graduationYear;
        private Boolean isAttendUniversity;
        private String educationDescription;
    }
}
