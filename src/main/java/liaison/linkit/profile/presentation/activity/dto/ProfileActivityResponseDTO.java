package liaison.linkit.profile.presentation.activity.dto;

import java.util.ArrayList;
import java.util.List;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ProfileActivityResponseDTO {

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ProfileActivityItems {
        @Builder.Default
        private List<ProfileActivityItem> profileActivityItems = new ArrayList<>();
    }

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ProfileActivityItem {
        private Long profileActivityId;
        private String activityName;
        private String activityRole;
        private String activityStartDate;
        private String activityEndDate;
    }

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ProfileActivityDetail {
        private Long profileActivityId;
        private String activityName;
        private String activityRole;
        private String activityStartDate;
        private String activityEndDate;
        private Boolean isActivityInProgress;
        private String activityDescription;

        // 증명서 및 인증 정보 변수 추가
        private Boolean isActivityCertified;
        private Boolean isActivityVerified;
        private String activityCertificationAttachFileName;
        private String activityCertificationAttachFilePath;
    }

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class AddProfileActivityResponse {
        private Long profileActivityId;
        private String activityName;
        private String activityRole;
        private String activityStartDate;
        private String activityEndDate;
        private Boolean isActivityInProgress;
        private String activityDescription;
    }

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UpdateProfileActivityResponse {
        private Long profileActivityId;
        private String activityName;
        private String activityRole;
        private String activityStartDate;
        private String activityEndDate;
        private Boolean isActivityInProgress;
        private String activityDescription;
    }

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ProfileActivityCertificationResponse {
        private Boolean isActivityCertified;
        private Boolean isActivityVerified;
        private String activityCertificationAttachFileName;
        private String activityCertificationAttachFilePath;
    }

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class RemoveProfileActivityCertificationResponse {
        private Long profileActivityId;
    }

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class RemoveProfileActivityResponse {
        private Long profileActivityId;
    }
}
