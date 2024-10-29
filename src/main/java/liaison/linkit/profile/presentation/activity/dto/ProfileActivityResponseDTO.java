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
        private boolean isActivityInProgress;
        private String activityDescription;

        // 증명서 및 인증 정보 변수 추가
    }
}
