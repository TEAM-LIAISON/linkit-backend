package liaison.linkit.profile.presentation.activity.dto;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ProfileActivityRequestDTO {

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class AddProfileActivityRequest {
        private String activityName;
        private String activityRole;
        private String activityStartDate;
        private String activityEndDate;
        private Boolean isActivityInProgress;
        private String activityDescription;
    }
}
